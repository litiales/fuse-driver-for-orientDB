import ovirtualfs.Functions;
import ovirtualfs.IntWrapper;
import ovirtualfs.OVFSManager;

import java.util.Date;

public class test {

    public static void main(String args[]) {
        OVFSManager ovfsManager = OVFSManager.getOVFSHandler("./", "test");
        //ovfsManager.initializeDB();
        //ovfsManager.close();
        ovfsManager.ls();
        System.out.println(ovfsManager.getFunctionHandler().getattr("/litiales", new IntWrapper()).toString());
        ovfsManager.ls();
        ovfsManager.close();
    }

}
