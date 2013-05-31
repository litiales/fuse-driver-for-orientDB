package ovirtualfs;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import ovirtualfs.OVFSManager;


public class Functions {

    private OrientGraph fileSystem;
    private ODatabaseBrowser databaseBrowser;

    Functions(OrientGraph fileSystem, ODatabaseBrowser databaseBrowser) {
        this.fileSystem = fileSystem;
        this.databaseBrowser = databaseBrowser;
    }

    int mknod (String path) {
        if
    }

}
