import ovirtualfs.OVFSManager;

public class test {

    public static void main(String args[]) {
        OVFSManager ovfsManager = OVFSManager.getOVFSHandler("/home/litiales/Desktop/", "test");
        //ovfsManager.initializeDB();
        System.out.println(ovfsManager.databaseBrowser.getResourcePath("/home/litiales/sfdgdfg"));
        ovfsManager.close();
    }

}
