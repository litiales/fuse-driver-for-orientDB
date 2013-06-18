import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ovirtualfs.Functions;
import ovirtualfs.IntWrapper;
import ovirtualfs.OVFSManager;

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
          ovfsManager.close();
//        System.out.println(",a,b,c".split(",")[0]);

    }

}
