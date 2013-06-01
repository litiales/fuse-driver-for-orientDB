package ovirtualfs;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ovirtualfs.OVFSManager;
import ovirtualfs.resources.Stat;

import java.util.Date;
import java.util.Iterator;


public class Functions {

    static final int ENOENT          = -1; //No souch file or directory
    static final int EEXIST          = -2; //File already exists
    static final int EISDIR          = -3; //Requesting directory operations on a file
    static final int EPERM           = -4; //Trying to do a not permetted action
    static final int ENOTDIR         = -20; //Trying to use directories functions on a resource that is not a directory
    static final int ENOTEMPTY       = -21; //Tryng to call rmdir against a non empty directory

    static final String FILE_NOD = "class:File";
    static final String DIR_NOD = "class:Directory";

    private OrientGraph fileSystem;
    private ODatabaseBrowser databaseBrowser;

    Functions(OrientGraph fileSystem, ODatabaseBrowser databaseBrowser) {
        this.fileSystem = fileSystem;
        this.databaseBrowser = databaseBrowser;
    }

    /**
     * @param path The path to the resource you want to get attributes from
     * @param ret_value The variable where to put errno if error occurs
     * @return The stat file
     */
    public Stat getattr (String path, IntWrapper ret_value) {

        OrientVertex resourceNode;
        resourceNode = databaseBrowser.getResource(path, ret_value);

        if (resourceNode == null)
            return null;

        return resourceNode.getProperty("stat");

    }

    /**
     * @param path The link to the resource to follow
     * @param ret_value The variable where to put errno if error occurs
     * @return
     */
    public String readlink (String path, IntWrapper ret_value) {

        OrientVertex resourceNode;
        resourceNode = databaseBrowser.getResource(path, ret_value);

        if (resourceNode == null)
            return null;

        if (!resourceNode.getLabel().equals("Link")) {
            ret_value.value = Functions.EPERM;
            System.out.println("[Error] Readlink on a non link resource");
            return null;
        }

        return resourceNode.getProperty("linkedResource");

    }

    /**
     * This function is called for both mknod and mkdir
     * @param path the COMPLETE path to the resource, I will extract from it (file/dir)name, no problem.
     * @param mode the mode passed from fuse.
     * @param uid the user id.
     * @param gid the group id.
     * @param type use static field FIELD_NOD or DIR_NOD
     * @return 0 if no error occorred, the errno if an error occurred
     * @throws RuntimeException if an error occurred in type param
     */
    public int create_resource (String path, String mode, String uid, String gid, String type) throws RuntimeException {

        //Sto tentando di creare una risorsa non meglio specificata
        if(type != FILE_NOD && type != DIR_NOD)
            throw new RuntimeException("Requested for a not file nor directory node");

        //I file devono terminare senza /
        if (path.endsWith("/") && type.equals(FILE_NOD))
            return EISDIR;

        //Le directory devono terminare con /
        if (!path.endsWith("/") && type.equals(DIR_NOD))
            path = path + "/";

        OrientVertex parentNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        parentNode = databaseBrowser.getResourcePath(path, ret_value);
        if (parentNode == null) //La risorsa richiesta gia' esiste o e' stata richiesta in un path insesistente
            return ret_value.value;

        OrientVertex newResource;
        newResource = fileSystem.addVertex("class:File");
        String fileName;
        String[] canonicalPath = path.split("/");
        fileName = canonicalPath[canonicalPath.length - 1];
        initializeNewNode(newResource, mode, uid, gid, fileName);
        fileSystem.addEdge(null, parentNode, newResource, fileName);
        return 0; //everithing is ok
    }

    /**
     * @param path the complete path to the resource which unlink has to been called against
     * @return 0 if no error occurred
     *         ENOENT if the resource is a directory
     */
    public int removeResource (String path) {

        OrientVertex resourceNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        resourceNode = databaseBrowser.getResourcePath(path, ret_value);

        if (resourceNode == null)
            return ret_value.value;

        if (resourceNode.getLabel().equals("Directory"))
            return ENOENT;

        Iterator<Edge> iterator = resourceNode.getEdges(Direction.BOTH).iterator();
        while (iterator.hasNext())
            iterator.next().remove();
        resourceNode.remove();
        return 0;

    }

    /**
     * @param path the complete path to the resource which rmdir has to been called against
     * @return 0 if no error occurred,
     *         ENOTDIR if the resource is not a directory,
     *         ENOTEMPTY if the directory to remove is not empty
     */
    public int removeDirectory (String path) {

        OrientVertex resourceNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        resourceNode = databaseBrowser.getResourcePath(path, ret_value);

        if (!resourceNode.getLabel().equals("Directory"))
            return ENOTDIR;

        if (resourceNode.countEdges(Direction.OUT) != 0)
            return ENOTEMPTY;

        Iterator<Edge> iterator = resourceNode.getEdges(Direction.IN).iterator();
        while (iterator.hasNext())
            iterator.next().remove();
        resourceNode.remove();

        return 0;

    }

    public int link(String linkPath, String linkedResource) {
        
    }

    private void initializeNewNode (OrientVertex resource, String mode_t, String uid, String gid, String filename) {
        Date time;
        time = new Date();
        Stat statStruct;
        statStruct = new Stat(mode_t, uid, gid, 0, time);
        resource.setProperty("stat", statStruct);
        resource.setProperty("name", filename);
    }

}
