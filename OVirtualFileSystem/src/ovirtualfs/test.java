package ovirtualfs;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


import java.util.ArrayList;
import java.util.Date;

public class test {

    public static void main(String args[]) {

        OVFSManager ovfsManager = OVFSManager.getOVFSHandler("./", "test");
//        ovfsManager.drop();
//        ovfsManager.initializeDB();
          //ovfsManager.getFunctionHandler().write()
//        ovfsManager.close();
        ovfsManager.ls();
//        System.out.println(ovfsManager.getFunctionHandler().link("/litiales/ciao/link", "/litiales/ciao/", "litiales", "litiales"));
//        ovfsManager.getFunctionHandler().create_resource("/litiales", "0444", "litiales", "users", Functions.DIR_NOD);
//        ovfsManager.getFunctionHandler().create_resource("/litiales/ciao", "0444", "litiales", "users", Functions.DIR_NOD);
//        ovfsManager.getFunctionHandler().link("/litiales/link7", "/litiales/link", "litiales", "users");
//        System.out.println(ovfsManager.getFunctionHandler().getattr("/litiales/link", new IntWrapper()).ctime);
//        System.out.println(ovfsManager.getFunctionHandler().getattr("/litiales/", new IntWrapper()).atime.toString());
//        ovfsManager.ls();
//        System.out.println(",a,b,c".split(",")[0]);

        byte[] overwrite = {1,0,1,1, 0,1,0,1,0,0};
        Functions handler = ovfsManager.getFunctionHandler();
        //handler.write("/file", overwrite,8, 10, "litiales", "litiales");
        //handler.write("/file", overwrite,10, 10, "litiales", "litiales");
        //handler.truncate("/file", 7, "litiales", "litiales");
        //System.out.println(handler.write("/file", overwrite,0 , 10, "litiales", "litiales"));
        IntWrapper ret = new IntWrapper();
        byte[] test = handler.read("/file", 0, 11, "litiales", "litiales", ret);
        //System.out.println(ret.value);
        print(test);
        ovfsManager.close();
    }

    public static void print(byte[] test) {
        for (byte item : test)
            System.out.print(item);
        System.out.println();
    }

}
