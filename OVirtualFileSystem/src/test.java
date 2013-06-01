import ovirtualfs.Functions;
import ovirtualfs.OVFSManager;

import java.util.Date;

public class test {

    public static void main(String args[]) {
        OVFSManager ovfsManager = OVFSManager.getOVFSHandler("./", "test");
        ovfsManager.initializeDB();
        ovfsManager.close();
    }

}
