import ovirtualfs.OVFSManager;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/6/13
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class test1 {

    public static void main(String[] args) {
        OVFSManager.getOVFSHandler("test").initializeDB();
    }

}
