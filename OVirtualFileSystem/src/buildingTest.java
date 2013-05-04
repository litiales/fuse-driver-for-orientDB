import OVFSResources.OBaseResource;
import OVFSResources.ODirectory;
import OVFSResources.OFile;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Iterator;

/**
 * This is the first building test for OVirtual File System.
 * As all first tests, it's going to fail, miserably.
 * And.... yes, I'm optimistic :)
 * 10:08:27 first try, first null pointer exception
 * 10:10:54 It prints but not at all levels
 * 10:13:36 ls() after graph building works;
 * 10:15 I start to write ls() function from existing db
 * 10:18:15 now it works
 * 10:47:00 finished to write trie building algorithm, I'm going to test it :)
 * 10:48:58 first error. It takes two times the same dir name; Now I have to stop my job.
 * 11:27:00 my first success. All works as should
 */

public class buildingTest {

    private static OGraphDatabase database;

    public static void main(String[] args) {
        database = new OGraphDatabase("local:/home/litiales/fuse-driver-for-orientDB/testDB1");
        if (database.exists())
            database.open("admin", "admin");
        else {
            database.create();
            populatedatabase();
        }
        ODocument root;
        root = database.getRoot("/");
        ls("", root);
        OBaseResource rootDir = createTrie(root, null);
        rootDir.ls("");
        database.close();
    }

    private static void populatedatabase() {
        ODocument root;
        root = database.createVertex().field("name", "root").field("type", "d");
        ODocument usr;
        usr = database.createVertex().field("name", "usr").field("type", "d");
        database.createEdge(root, usr);
        ODocument home;
        home = database.createVertex().field("name", "home").field("type", "d");
        database.createEdge(root, home);
        ODocument litiales = database.createVertex().field("name", "litiales").field("type", "d");
        database.createEdge(home, litiales);
        ODocument video = database.createVertex().field("name", "video").field("type", "d");
        database.createEdge(litiales, video);
        ODocument tesi = database.createVertex().field("name", "tesi").field("type", "d");
        database.createEdge(litiales, tesi);
        ODocument java1 = database.createVertex().field("name", "java1.java").field("type", "f");
        database.createEdge(tesi, java1);
        ODocument test2 = database.createVertex().field("name", "test2.java").field("type", "f");
        database.createEdge(tesi, test2);
        ODocument bin = database.createVertex().field("name", "bin").field("type", "d");
        database.createEdge(usr, bin);
        ODocument javac = database.createVertex().field("name", "javac").field("type", "f");
        database.createEdge(bin, javac);
        ODocument java = database.createVertex().field("name", "java").field("type", "f");
        database.createEdge(bin, java);
        ODocument lib = database.createVertex().field("name", "lib").field("type", "d");
        database.createEdge(usr, lib);
        ODocument jvm = database.createVertex().field("name", "jvm").field("type", "d");
        database.createEdge(lib, jvm);
        ODocument javavm = database.createVertex().field("name", "javavm").field("type", "f");
        database.createEdge(jvm, javavm);
        ODocument gcc = database.createVertex().field("name", "gcc").field("type", "f");
        database.createEdge(lib, gcc);
        database.setRoot("/", root);
    }

    private static void ls(String path, ODocument root) {
        if (database.getOutEdges(root).isEmpty()) {
            System.out.println(path + "/" + root.field("name"));
            return;
        }
        Iterator<OIdentifiable> iterator = database.getOutEdges(root).iterator();
        while (iterator.hasNext()) {
            ls(path + "/" + root.field("name"), database.getInVertex(iterator.next()));
        }
    }

    private static OBaseResource createTrie(ODocument current, ODirectory root) {
        if (database.getOutEdges(current).isEmpty()) { //siamo su una foglia
            if (current.field("type").toString().equals("f"))
                return new OFile((String) current.field("name"), current.getIdentity(), root, database);
            return new ODirectory((String) current.field("name"), current.getIdentity(), root, database);
        } else {
            ODirectory currDir = new ODirectory((String) current.field("name"), current.getIdentity(), root, database);
            Iterator<OIdentifiable> iterator = database.getOutEdges(current).iterator();
            ODocument nextres;
            while (iterator.hasNext()) {
                nextres = database.getInVertex(iterator.next());
                currDir.addSubResource((String) nextres.field("name"), createTrie(nextres, currDir));
            }
            return currDir;
        }
    }

}
