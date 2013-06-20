package ovirtualfs;

import com.tinkerpop.blueprints.impls.orient.OrientVertex;

final class ModeManager {

    private ModeManager(){}

    static boolean canRead(OrientVertex resource, String uid, String gid) {

        if (uid.equals(gid) && uid.equals("root"))
            return true;

        String perm;
        perm = getPermission(resource, uid, gid);

        if (Integer.parseInt(perm) > 3)
            return true;

        System.out.println("[Error] Read not allowed");
        return false;

    }

    static boolean canWrite(OrientVertex resource, String uid, String gid) {

        if (uid.equals(gid) && uid.equals("root"))
            return true;

        String perm;
        perm = getPermission(resource, uid, gid);

        if (perm.equals("2") || perm.equals("3") || perm.equals("6") || perm.equals("7"))
            return true;

        System.out.println("[Error] Write not allowed");
        return false;

    }

    static boolean canExecute(OrientVertex resource, String uid, String gid) {

        if (uid.equals(gid) && uid.equals("root"))
            return true;

        String perm;
        perm = getPermission(resource, uid, gid);

        if (Integer.parseInt(perm) % 2 == 1)
            return true;

        System.out.println("[Error] Execute not allowed");
        return false;

    }

    static private String getPermission(OrientVertex resource, String uid, String gid) {

        String uOwn;
        uOwn = resource.getProperty("uid");
        String gOwn;
        gOwn = resource.getProperty("gid");
        String rMode;
        rMode = resource.getProperty("mode");
        String perm;

        if (uOwn.equals(uid) && gOwn.equals(gid))
            perm = rMode.substring(1, 2);

        else if (!uOwn.equals(uid) && gOwn.equals(gid))
            perm = rMode.substring(2, 3);

        else
            perm = rMode.substring(3, 4);

        return perm;

    }

}
