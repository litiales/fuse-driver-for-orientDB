//TODO check when atime ctime and mtime is modified

package ovirtualfs;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ovirtualfs.resources.Stat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class Functions {

    public static final String FILE_NOD = "class:File";
    public static final String DIR_NOD = "class:Directory";
    public static final String LINK = "class:Link";

    static final int ENOENT = -1; //No souch file or directory
    static final int EEXIST = -2; //File already exists
    static final int EISDIR = -3; //Requesting directory operations on a file
    static final int EPERM = -4; //Trying to do a not permetted action
    static final int ENOTDIR = -5; //Trying to use directories functions on a resource that is not a directory
    static final int ENOTEMPTY = -6; //Tryng to call rmdir against a non empty directory
    static final int EINVAL = -7; //Using invalid arguments
    static final int EXDEV = -18; //Invalid cross-device link
    static final int EOF = -8;

    private static int CHUNK_SIZE = 4; //8192 8kB size

    private OrientGraph fileSystem;
    private ODatabaseBrowser databaseBrowser;

    Functions(OrientGraph fileSystem, ODatabaseBrowser databaseBrowser) {
        this.fileSystem = fileSystem;
        this.databaseBrowser = databaseBrowser;
    }

    /**
     * @param path      The path to the resource you want to get attributes from
     * @param ret_value The variable where to put errno if error occurs
     * @return The stat file
     */
    public Stat getattr(String path, IntWrapper ret_value, String uid, String gid) {

        OrientVertex resourceNode;
        resourceNode = databaseBrowser.getResource(path, ret_value, uid, gid);

        if (resourceNode == null)
            return null;

        if (resourceNode.getLabel().equals("Link")) {
            while (resourceNode.getVertices(Direction.OUT, "link").iterator().hasNext())
                resourceNode = (OrientVertex) resourceNode.getVertices(Direction.OUT, "link").iterator().next();
        }

        if (resourceNode.getLabel().equals("Link"))
            return null;

        if (resourceNode == null)
            return null;

        return new Stat((String) resourceNode.getProperty("mode"),
                        (String) resourceNode.getProperty("uid"),
                        (String) resourceNode.getProperty("gid"),
                        (Integer) resourceNode.getProperty("size"),
                        (Date) resourceNode.getProperty("atime"),
                        (Date) resourceNode.getProperty("mtime"),
                        (Date) resourceNode.getProperty("ctime"),
                        resourceNode.getLabel().equals("Link") ? (String) resourceNode.getProperty("linkedType") : resourceNode.getLabel());

    }

    /**
     * @param path      The link to the resource to follow
     * @param ret_value The variable where to put errno if error occurs
     * @return
     */
    public String readlink(String path, IntWrapper ret_value, String uid, String gid) {

        OrientVertex resourceNode;
        resourceNode = databaseBrowser.getResource(path, ret_value, uid, gid);

        if (resourceNode == null)
            return null;

        if (!resourceNode.getLabel().equals("Link")) {
            ret_value.value = Functions.EINVAL;
            System.out.println("[Error] Readlink on a non link resource");
            return null;
        }

        return resourceNode.getProperty("linked");
    }

    /**
     * This function is called for both mknod and mkdir
     *
     * @param path the COMPLETE path to the resource, I will extract from it (file/dir)name, no problem.
     * @param mode the mode passed from fuse.
     * @param uid  the user id.
     * @param gid  the group id.
     * @param type use static field FIELD_NOD or DIR_NOD
     * @return 0 if no error occorred, the errno if an error occurred
     * @throws RuntimeException if an error occurred in type param
     */
    public int create_resource(String path, String mode, String uid, String gid, String type) throws RuntimeException {

        if (path.equals("/"))
            return EPERM;

        //Sto tentando di creare una risorsa non meglio specificata
        if (type != FILE_NOD && type != DIR_NOD)
            throw new RuntimeException("Requested for a not file nor directory node");

        //I file devono terminare senza /
        if (path.endsWith("/") && type.equals(FILE_NOD))
            return EISDIR;

        OrientVertex parentNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        parentNode = databaseBrowser.getResourcePath(path, ret_value, uid, gid);

        if (parentNode == null) //La risorsa richiesta gia' esiste o e' stata richiesta in un path insesistente
            return ret_value.value;

        if (!ModeManager.canExecute(parentNode, uid, gid) || !ModeManager.canWrite(parentNode, uid, gid))
            return EPERM;

        Date now;
        now = new Date();
        parentNode.setProperty("mtime", now);
        parentNode.setProperty("ctime", now);

        OrientVertex newResource;
        newResource = fileSystem.addVertex(type);
        String fileName;
        String[] canonicalPath = path.split("/");
        fileName = canonicalPath[canonicalPath.length - 1];
        initializeNewNode(newResource, mode, uid, gid, type.equals(DIR_NOD) ? fileName + "/" : fileName);
        fileSystem.addEdge(null, parentNode, newResource, fileName);

        fileSystem.commit();
        return 0; //everithing is ok
    }

    /**
     * It is a wrapper of unlink
     *
     * @param path the complete path to the resource which unlink has to been called against
     * @return 0 if no error occurred
     *         ENOENT if the resource is a directory
     */
    public int removeResource(String path, String uid, String gid) {

        if (path.endsWith("/")) {
            System.out.println("[Error] Unilnk requested for a directory request. Use rmdir instead.");
            return EISDIR;
        }

        OrientVertex resourceNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        resourceNode = databaseBrowser.getResource(path, ret_value, uid, gid);

        if (resourceNode == null)
            return ret_value.value;

        OrientVertex parent;
        parent = (OrientVertex) resourceNode.getVertices(Direction.IN, (String) resourceNode.getProperty("name")).iterator().next();
        if (!ModeManager.canWrite(parent, uid, gid))
            return EPERM;

        if (resourceNode.getLabel().equals("Directory")) {
            System.out.println("[Error] Unilnk requested for a directory request. Use rmdir instead.");
            return EISDIR;
        }

        Date now;
        now = new Date();
        parent.setProperty("ctime", now);
        parent.setProperty("mtime", now);

        Iterator<Edge> iterator = resourceNode.getEdges(Direction.BOTH).iterator();
        while (iterator.hasNext())
            iterator.next().remove();
        resourceNode.remove();

        fileSystem.commit();
        return 0;
    }

    /**
     * @param path the complete path to the resource which rmdir has to been called against
     * @return 0 if no error occurred,
     *         ENOTDIR if the resource is not a directory,
     *         ENOTEMPTY if the directory to remove is not empty
     */
    public int removeDirectory(String path, String uid, String gid) {

        OrientVertex resourceNode;
        IntWrapper ret_value;
        ret_value = new IntWrapper();
        resourceNode = databaseBrowser.getResource(path, ret_value, uid, gid);

        if (resourceNode == null)
            return ret_value.value;

        OrientVertex parent;
        parent = (OrientVertex) resourceNode.getVertices(Direction.IN, (String) resourceNode.getProperty("name")).iterator().next();
        if (!ModeManager.canWrite(parent, uid, gid))
            return EPERM;

        if (!resourceNode.getLabel().equals("Directory"))
            return ENOTDIR;

        if (resourceNode.countEdges(Direction.OUT) != 0)
            return ENOTEMPTY;

        Date now;
        now = new Date();
        parent.setProperty("ctime", now);
        parent.setProperty("mtime", now);

        Iterator<Edge> iterator = resourceNode.getEdges(Direction.IN).iterator();
        while (iterator.hasNext())
            iterator.next().remove();
        resourceNode.remove();

        fileSystem.commit();
        return 0;
    }

    public int link(String linkPath, String linkedResource, String uid, String gid) {

        IntWrapper ret_val;
        ret_val = new IntWrapper();

        OrientVertex linkingParent;
        linkingParent = databaseBrowser.getResourcePath(linkPath, ret_val, uid, gid);

        if (linkingParent == null)
            return ret_val.value;

        if (!ModeManager.canExecute(linkingParent, uid, gid) || !ModeManager.canWrite(linkingParent, uid, gid)) {
            return EPERM;
        }

        OrientVertex linked;
        linked = databaseBrowser.getResource(linkedResource, ret_val, uid, gid);

        if (linked == null)
            return ret_val.value;

        Date now;
        now = new Date();
        linkingParent.setProperty("mtime", now);
        linkingParent.setProperty("ctime", now);
        linked.setProperty("ctime", now);

        OrientVertex linking;
        linking = fileSystem.addVertex("class:Link");
        String[] canonicalPath = linkPath.split("/");
        String linkName;
        linkName = canonicalPath[canonicalPath.length - 1];
        initializeNewLink(linking, linked, linkName, linkedResource);
        fileSystem.addEdge(null, linkingParent, linking, linkName);

        linked.removeProperty("ctime"); //aggiorno perchè in link ctime ciene aggiornato
        linked.setProperty("ctime", now);

        fileSystem.addEdge(null, linking, linked, "link");

        fileSystem.commit();
        return 0;
    }

    /**
     * @param resourcePath The path of the resource you want to rename
     * @param newName      The new name to give to the pointed resource
     * @param uid          The user who is requesting the rename
     * @param gid          The group of the user who is requesting the rename
     * @return 0 if the rename is completed with no errors
     */
    public int rename(String resourcePath, String newName, String uid, String gid) {

        if (newName.startsWith("/"))
            return EXDEV;

        IntWrapper ret_val;
        ret_val = new IntWrapper();

        OrientVertex resource;
        resource = databaseBrowser.getResource(resourcePath, ret_val, uid, gid);

        if (!resource.getLabel().equals("Directory") && newName.endsWith("/"))
            return ENOTDIR;

        if (resource.getLabel().equals("Directory"))
            newName = newName.endsWith("/") ? newName : newName + "/";

        OrientVertex parent;
        parent = (OrientVertex) resource.getVertices(Direction.IN, (String) resource.getProperty("name")).iterator().next();
        if (!ModeManager.canWrite(parent, uid, gid))
            return EPERM;

        String[] canonicalPath;
        canonicalPath = resourcePath.split("/");
        String oldName;
        oldName = canonicalPath[canonicalPath.length - 1];
        if (parent.countEdges(Direction.OUT, newName) == 0) {

            resource.setProperty("name", newName); //effettuo un semplice rename

            parent.getEdges(Direction.OUT, oldName).iterator().next().remove();
            fileSystem.addEdge(null, parent, resource, newName);

        } else {

            OrientVertex otherRes;
            otherRes = (OrientVertex) parent.getVertices(Direction.OUT, newName).iterator().next();
            if (resource.getLabel().equals("Directory") && !otherRes.getLabel().equals("Directory"))
                return ENOTDIR;
            if (!resource.getLabel().equals("Directory") && otherRes.getLabel().equals("Directory"))
                return EISDIR;
            if (resource.getLabel().equals("Directory") && otherRes.countEdges(Direction.OUT) != 0)
                return ENOTEMPTY;

            Iterator<Edge> iterator;

            iterator = resource.getEdges(Direction.IN).iterator();
            while (iterator.hasNext()) {
                iterator.next().remove();
            }

            iterator = otherRes.getEdges(Direction.IN).iterator();
            OrientEdge currEdge;
            OrientVertex tempVertex;
            String tempLabel;
            while (iterator.hasNext()) {
                currEdge = (OrientEdge) iterator.next();
                tempVertex = currEdge.getVertex(Direction.OUT);
                tempLabel = currEdge.getLabel();
                currEdge.remove();
                fileSystem.addEdge(null, tempVertex, resource, tempLabel);
            }

            resource.setProperty("name", newName);
            otherRes.remove();

        }

        Date now;
        now = new Date();
        parent.setProperty("mtime", now);
        parent.setProperty("ctime", now);
        resource.setProperty("atime", now);
        resource.setProperty("ctime", now);

        fileSystem.commit();
        return 0;

    }

    /**
     * @param resourcePath
     * @param mode
     * @param uid
     * @param gid
     * @return
     */
    public int chmod(String resourcePath, String mode, String uid, String gid) {

        OrientVertex resource;
        IntWrapper ret_val;
        ret_val = new IntWrapper();
        resource = getResourceForCh(resourcePath, uid, gid, ret_val);

        if (resource == null)
            return ret_val.value;

        Date now;
        now = new Date();
        resource.setProperty("ctime", now);

        resource.setProperty("mode", mode);
        fileSystem.commit();
        return 0;

    }

    /**
     * @param resourcePath
     * @param newUser
     * @param newGroup
     * @param uid
     * @param gid
     * @return
     */
    public int chown(String resourcePath, String newUser, String newGroup, String uid, String gid) {

        OrientVertex resource;
        IntWrapper ret_val;
        ret_val = new IntWrapper();
        resource = getResourceForCh(resourcePath, uid, gid, ret_val);

        if (resource == null)
            return ret_val.value;

        Date now;
        now = new Date();
        resource.setProperty("ctime", now);

        resource.setProperty("uid", newUser);
        resource.setProperty("gid", newGroup);
        fileSystem.commit();
        return 0;

    }

    private OrientVertex getResourceForCh(String resourcePath, String uid, String gid, IntWrapper ret) {

        OrientVertex resource;
        IntWrapper ret_val;
        ret_val = new IntWrapper();
        resource = databaseBrowser.getResource(resourcePath, ret, uid, gid);
        if (resource == null)
            return null;

        if (resource.getLabel().equals("Link")) {
            while (resource.getVertices(Direction.OUT, "link").iterator().hasNext())
                resource = (OrientVertex) resource.getVertices(Direction.OUT, "link").iterator().next();
        }

        if (resource.getLabel().equals("Link"))
            return null;

        if (resource == null)
            return null;

        if (!uid.equals("root") || !gid.equals("root")) {

            String owner;
            owner = resource.getProperty("uid");
            String group;
            group = resource.getProperty("gid");

            if (!uid.equals(owner) || !gid.equals(group)) {
                System.out.println("[Error] Permission denied");
                ret.value = EPERM;
                return null;
            }

        }

        return resource;

    }

    public int write(String path, byte[] data, int offset, int size, String user, String group) {

        OrientVertex resource;
        IntWrapper ret_val;
        ret_val = new IntWrapper();
        resource = databaseBrowser.getResource(path, ret_val, user, group);
        if (resource == null && offset == 0)
            if (ret_val.value != ENOENT && databaseBrowser.getResourcePath(path, ret_val, user, group) == null)
                return ret_val.value;
            else {
                if (offset != 0)
                    return EOF;
                int err;
                if ((err = create_resource(path, String.valueOf(0744), user, group, FILE_NOD)) == 0)
                    return write(path, data, offset, size, user, group);
                else
                    return err;
            }

        if (resource.getLabel().equals("Link")) {
            while (resource.getVertices(Direction.OUT, "link").iterator().hasNext())
                resource = (OrientVertex) resource.getVertices(Direction.OUT, "link").iterator().next();
        }

        if (resource.getLabel().equals("Link"))
            return ENOENT;

        if (resource.getLabel().equals("Directory"))
            return EISDIR;

        if (ModeManager.canWrite(resource, user, group) == false)
            return EPERM;

        int fileSize = (Integer) resource.getProperty("size");

        if (fileSize / 8 < offset)
            return EOF;

        try {
            ORecordBytes startRecord;
            ArrayList<ORID> list;
            list = resource.getProperty("data");
            if (list == null)
                list = new ArrayList<ORID>();

            byte[] fill = new byte[CHUNK_SIZE];
            int recordNum = list.size();
            ORecordBytes record;
            while (recordNum < (offset + size) % CHUNK_SIZE) {
                record = new ORecordBytes();
                record.fromStream(fill);
                list.add(record.getIdentity());
                recordNum++;
            }

            int index = 0;
            int recordIndex = offset / CHUNK_SIZE;
            byte[] toBeWritten = fillArray(list, size, offset, fileSize, data);
            int totalBytes = toBeWritten.length;
            byte[] writingBytes = new byte[CHUNK_SIZE];

            while (totalBytes > index) {
                System.arraycopy(toBeWritten, index, writingBytes, 0, CHUNK_SIZE);
                record = (ORecordBytes) list.get(recordIndex++).getRecord();
                record.fromStream(writingBytes);
                index += CHUNK_SIZE;
            }

            Date now = new Date();

            resource.setProperty("size", offset + size > fileSize ? (offset + size) * 8 : fileSize);
            resource.setProperty("mtime", now);
            resource.setProperty("ctime", now); //TODO does it change???

        } catch (Exception e) {

            fileSystem.rollback();
            return EOF;

        }

        fileSystem.commit();
        return size;

    }

    public int truncate(String path, int size, String user, String group) {

        OrientVertex resource;
        IntWrapper ret_val;
        ret_val = new IntWrapper();
        resource = databaseBrowser.getResource(path,ret_val, user, group);

        if (resource == null)
            return ret_val.value;

        if (!ModeManager.canWrite(resource, user, group))
            return EPERM;

        ArrayList<ORID> list;
        list = resource.getProperty("data");

        if (list == null)
            list = new ArrayList<ORID>();

        int currSize;
        currSize = list.size();
        int target = size/CHUNK_SIZE;
        if (size % CHUNK_SIZE != 0)
            target++;

        int currEl = currSize - 1;

        if (target < currSize)
            while (target < currSize)
                list.remove(currEl);

        if (target > currSize){
            byte[] init = new byte[CHUNK_SIZE];
            ORecordBytes record;
            while (target > currSize) {
                record = new ORecordBytes();
                record.fromStream(init);
                list.add(record.getIdentity());
            }

            record = list.get(target).getRecord();
            init = record.toStream();

            int toSave = size % CHUNK_SIZE;
            byte[] newRecord = new byte[CHUNK_SIZE];
            System.arraycopy(toSave, 0, newRecord, 0, toSave);

            record.fromStream(newRecord);

        }

        resource.setProperty("size", size * 8);
        Date now = new Date();

        resource.setProperty("mtime", now);
        resource.setProperty("ctime", now); //TODO does it change???

        return 0;

    }

    private byte[] fillArray(ArrayList<ORID> list, int size, int off, int filesize, byte[] data) {

        int startR = off / CHUNK_SIZE;
        int endR = (off + size) / CHUNK_SIZE;
        int endElementStart = filesize - (off + size) % CHUNK_SIZE; //the position of the element to append
        int startEl = off % CHUNK_SIZE;
        int offReal = (size + off) % CHUNK_SIZE;
        if ((off + size) % CHUNK_SIZE == 0)
            endR--;

        int sizeTotal = (endR - startR + 1) * CHUNK_SIZE;
        byte[] ret = new byte[sizeTotal];

        System.arraycopy(((ORecordBytes) list.get(startR).getRecord()).toStream(), 0, ret, 0, startEl);
        System.arraycopy(data, 0, ret, startEl, size);
        System.arraycopy(((ORecordBytes) list.get(endR).getRecord()).toStream(), offReal, ret, size + startEl, sizeTotal - size - offReal);

        return ret;

    }

    private void initializeNewLink(OrientVertex linking, OrientVertex linked, String linkName, String linkedResource) {
        linking.setProperty("name", linkName);
        linking.setProperty("linked", linkedResource);
        linking.setProperty("linkedType", linked.getLabel().equals("Link") ? linked.getProperty("linkedType") : linked.getLabel());
    }

    private void initializeNewNode(OrientVertex resource, String mode, String uid, String gid, String filename, Date time) {
        resource.setProperty("name", filename);
        resource.setProperty("mode", mode);
        resource.setProperty("uid", uid);
        resource.setProperty("gid", gid);
        resource.setProperty("size", 0);
        resource.setProperty("atime", time);
        resource.setProperty("ctime", time);
        resource.setProperty("mtime", time);
    }

    private void initializeNewNode(OrientVertex resource, String mode, String uid, String gid, String filename) {
        initializeNewNode(resource, mode, uid, gid, filename, new Date());
    }

}
