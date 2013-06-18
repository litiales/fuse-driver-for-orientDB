package ovirtualfs;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Iterator;

public class ODatabaseBrowser {

    private OrientGraph graphDatabase;
    private OrientVertex root;
    private String pwdString;


    ODatabaseBrowser(OrientGraph graphDatabase, OrientVertex root) {
        this.graphDatabase = graphDatabase;
        this.root = root;
        pwdString = "/";
    }

    public void deepLs(Vertex currRoot, String currPath) {

        if (((OrientVertex) currRoot).getLabel().equals("Link")) {
            System.out.println(((OrientVertex) currRoot).getLabel() + " " + currPath + currRoot.getProperty("name") + " -> " + currRoot.getProperty("linked"));
            return;
        }
        Iterator<Vertex> iterator = currRoot.getVertices(Direction.OUT).iterator();
        if (iterator.hasNext()) {
            while (iterator.hasNext())
                deepLs(iterator.next(), currPath + currRoot.getProperty("name"));
        } else
            System.out.println(((OrientVertex) currRoot).getLabel() + " " + currPath + currRoot.getProperty("name"));

    }

    /**
     * @param path     The path were create the new resource (i.e. if you want to create /home/litiales/test you have to pass the complete path, not only /home/litiales/)
     * @param retValue The Integer variable where put errcode if an error occurred
     * @return The parent vertex of the new resource, or null if an error occurred
     */
    public OrientVertex getResourcePath(String path, IntWrapper retValue, String uid, String gid) {

        OrientVertex parent;
        String[] canonicalPath;
        canonicalPath = path.split("/");
        parent = getParentResource(canonicalPath, retValue, uid, gid);

        if (parent == null)
            return null;

        String resourceName;
        resourceName = canonicalPath[canonicalPath.length - 1];
        if (parent.countEdges(Direction.OUT, resourceName + "/") == 0 && parent.countEdges(Direction.OUT, resourceName) == 0)
            return parent;

        System.out.println("[Error] Requested for path " + path + " but it already exists");
        retValue.value = Functions.EEXIST;
        return null;

    }

    /**
     * @param path     The path were create the new resource (i.e. if you want to stat /home/litiales/test you have to pass the complete path, not only /home/litiales/)
     * @param retValue The Integer variable where put errcode if an error occurred
     * @return The vertex of the resource to stat, or null if an error occurred
     */
    public OrientVertex getResource(String path, IntWrapper retValue, String uid, String gid) {

        OrientVertex parent;
        String[] canonicalPath;
        canonicalPath = path.split("/");
        parent = getParentResource(canonicalPath, retValue, uid, gid);

        if (parent == null)
            return null;

        if (!ModeManager.canExecute(parent, uid, gid)) {
            retValue.value = Functions.EPERM;
            return null;
        }


        String resourceName;
        resourceName = canonicalPath[canonicalPath.length - 1];
        Iterator<Vertex> iterator;
        iterator = parent.getVertices(Direction.OUT, resourceName).iterator();

        if (iterator.hasNext())
            return (OrientVertex) iterator.next();

        System.out.println("[Error] Requested a not yet created resource");
        retValue.value = Functions.ENOENT;
        return null;

    }

    private OrientVertex getParentResource(String[] canonicalPath, IntWrapper retValue, String uid, String gid) {

        int pathLenght = canonicalPath.length;

        if (pathLenght == 1) {
            System.out.println("[Error] Requested access to root. Access denied.");
            retValue.value = Functions.EPERM;
            return null;
        }

        int index = 1; //il primo elemento e' la root
        OrientVertex parentResource;
        parentResource = root;
        Iterator<Vertex> iterator;

        while (index + 1 < pathLenght && parentResource != null) {

            if (!ModeManager.canExecute(parentResource, uid, gid)) { //Non posso fare il browse in una cartella su cui non ho i permessi
                retValue.value = Functions.EPERM;
                return null;
            }

            iterator = parentResource.getVertices(Direction.OUT, canonicalPath[index]).iterator();
            parentResource = iterator.hasNext() ? (OrientVertex) iterator.next() : null;

            if (parentResource == null) { //non c'è alcun nodo intermedio
                System.out.println("[Error] Request for not yet created directory");
                retValue.value = Functions.ENOENT;
            } else if (!parentResource.getLabel().equals("Directory")) { //controllo che il nodo su cui mi trovo sia effettivamente una directory
                System.out.println("[Error] Request for not yet created directory");
                retValue.value = Functions.ENOENT;
                parentResource = null;
            }

            index++;

        }

        return parentResource;

    }

}