package ovirtualfs;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import sun.security.provider.certpath.Vertex;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/6/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class OVFSManager {

    private OGraphDatabase rawGraphDatabase;
    private OrientGraph graphDatabase;
    private ODatabaseBrowser databaseBrowser;
    private OrientVertex root;
    private OResourceBuilder resourceBuilder;

    //region Costructors

    public static OVFSManager getOVFSHandler (String dbPath, String vfsName, String iUser, String iPassword) {
        dbPath = (dbPath.endsWith("/") ? dbPath : dbPath + "/") + vfsName;
        OVFSManager newVFS;
        if ((newVFS = dbPath.matches(".*[^\\w -.].*") ? new OVFSManager(dbPath) : null) == null)
            return null;
        return OVFSManager.openDB(newVFS, iUser, iPassword);
    }

    public static OVFSManager getOVFSHandler (String dbPath, String vfsName) {
        return OVFSManager.getOVFSHandler(dbPath, vfsName, "admin", "admin");
    }

    public static OVFSManager getOVFSHandler (String vfsName) {
        return OVFSManager.getOVFSHandler("./", vfsName, "admin", "admin");
    }

    public static OVFSManager getOVFSHandler (String vfsName, String iUser, String iPassword) {
        return OVFSManager.getOVFSHandler("./", vfsName, iUser, iPassword);
    }

    private static OVFSManager openDB(OVFSManager newVFS, String iUser, String iPassword) {
        boolean toBeInitialized = false;
        if (newVFS.rawGraphDatabase.exists()) {
            try {
                newVFS.rawGraphDatabase.open("admin", "admin");
            } catch (RuntimeException re) {
                return null;
            }
        } else {
            newVFS.rawGraphDatabase.create();
            toBeInitialized = true;
        }
        newVFS.graphDatabase = new OrientGraph(newVFS.rawGraphDatabase);
        if (toBeInitialized) {
            newVFS.graphDatabase.createVertexType("Directory");
            newVFS.graphDatabase.createVertexType("File");
            newVFS.graphDatabase.createVertexType("Link");
            ODocument aRoot;
            aRoot = newVFS.rawGraphDatabase.createVertex("Directory");
            aRoot.field("name", "/");
            aRoot.save();
            newVFS.rawGraphDatabase.setRoot("root", aRoot);
        }
        newVFS.root = newVFS.graphDatabase.getVertex(newVFS.rawGraphDatabase.getRoot("root").getIdentity());
        newVFS.databaseBrowser = new ODatabaseBrowser(newVFS.graphDatabase, newVFS.root);
        return newVFS;
    }

    private OVFSManager (String vfsPath) {
        rawGraphDatabase = new OGraphDatabase("local:" + vfsPath);
    }

    //endregions

    public void initializeDB() {
        OrientVertex usr = graphDatabase.addVertex("class:Directory");
        usr.setProperty("name", "usr/");
        graphDatabase.addEdge(null, root, usr, "usr");
        OrientVertex bin = graphDatabase.addVertex("class:Directory");
        bin.setProperty("name", "bin/");
        graphDatabase.addEdge(null, usr, bin, "bin");
        OrientVertex lib = graphDatabase.addVertex("class:Directory");
        lib.setProperty("name", "lib/");
        graphDatabase.addEdge(null, usr, lib, "lib");
        OrientVertex jvm = graphDatabase.addVertex("class:File");
        jvm.setProperty("name", "jvm");
        graphDatabase.addEdge(null, bin, jvm, "jvm");
        OrientVertex jconsole = graphDatabase.addVertex("class:File");
        jconsole.setProperty("name", "jconsole");
        graphDatabase.addEdge(null, lib, jconsole, "jconsole");
        OrientVertex otherjvm = graphDatabase.addVertex("class:Directory");
        otherjvm.setProperty("name", "otherjvm/");
        graphDatabase.addEdge(null, lib, otherjvm, "otherjvm");
        OrientVertex home = graphDatabase.addVertex("class:Directory");
        home.setProperty("name", "home/");
        graphDatabase.addEdge(null, root, home, "home/");
        OrientVertex litiales = graphDatabase.addVertex("class:Directory");
        litiales.setProperty("name", "litiales/");
        graphDatabase.addEdge(null, home, litiales, "litiales");
        OrientVertex fuse = graphDatabase.addVertex("class:Directory");
        fuse.setProperty("name", "fuse/");
        graphDatabase.addEdge(null, litiales, fuse, "fuse");
        OrientVertex tesi = graphDatabase.addVertex("class:Directory");
        tesi.setProperty("name", "tesi/");
        graphDatabase.addEdge(null, fuse, tesi, "tesi");
        OrientVertex progetto = graphDatabase.addVertex("class:File");
        progetto.setProperty("name", "progetto");
        graphDatabase.addEdge(null, tesi, progetto, "progetto");
        OrientVertex driver = graphDatabase.addVertex("class:File");
        driver.setProperty("name", "driver");
        graphDatabase.addEdge(null, tesi, driver, "driver");
        OrientVertex video = graphDatabase.addVertex("class:Directory");
        video.setProperty("name", "video/");
        graphDatabase.addEdge(null, litiales, video, "video");
        OrientVertex alessia = graphDatabase.addVertex("class:File");
        alessia.setProperty("name", "alessia");
        graphDatabase.addEdge(null, video, alessia, "alessia");
        OrientVertex alessia2 = graphDatabase.addVertex("class:File");
        alessia2.setProperty("name", "alessia2");
        graphDatabase.addEdge(null, video, alessia2, "alessia2");
        OrientVertex jvmL = graphDatabase.addVertex("class:Link");
        jvmL.setProperty("name", "jvmL");
        graphDatabase.addEdge(null, otherjvm, jvmL, "jvmL");
        graphDatabase.addEdge(null, jvmL, jvm, "link");
        databaseBrowser.deepLs(root, "");
        graphDatabase.drop();
    }

}
