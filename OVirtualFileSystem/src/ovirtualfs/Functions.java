package ovirtualfs;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ovirtualfs.OVFSManager;

import java.util.Date;


public class Functions {

    private static final int ENOENT = -1; //No souch file or directory

    private OrientGraph fileSystem;
    private ODatabaseBrowser databaseBrowser;

    Functions(OrientGraph fileSystem, ODatabaseBrowser databaseBrowser) {
        this.fileSystem = fileSystem;
        this.databaseBrowser = databaseBrowser;
    }

    int mknod (String path, String mode, String uid, String gid) {

        OrientVertex parentNode;
        parentNode = databaseBrowser.getResourcePath(path);
        if (parentNode == null) //La risorsa richiesta gia' esiste o e' stata richiesta in un path insesistente
            return ENOENT;

        OrientVertex newResource;
        newResource = fileSystem.addVertex("class:File");
        initializeNewNode(newResource, mode, uid, gid);

        return 0; //everithing is ok
    }

    private void initializeNewNode (OrientVertex resource, String mode_t, String uid, String gid) {
        Date time;
        time = new Date();
        resource.setProperty("mtime", time);
        resource.setProperty("atime", time);
        resource.setProperty("ctime", time);
        resource.setProperty("mode", mode_t);
        resource.setProperty("uid", uid);
        resource.setProperty("gid", gid);
    }

}
