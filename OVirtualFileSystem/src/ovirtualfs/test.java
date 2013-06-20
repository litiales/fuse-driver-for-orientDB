package ovirtualfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        //byte[] overwrite = {1,0,1,1, 0,1,0,1,0,0};
        Functions handler = ovfsManager.getFunctionHandler();
        //handler.write("/file1", overwrite,0, 10, "litiales", "litiales");
        //handler.write("/file1", overwrite,8, 10, "litiales", "litiales");
        //handler.truncate("/file1", 13, "litiales", "litiales");
        //System.out.println(handler.write("/file", overwrite,0 , 10, "litiales", "litiales"));
        IntWrapper ret = new IntWrapper();
        //byte[] test = handler.read("/file1", 3, 11, "litiales", "litiales", ret);
        //System.out.println(ret.value);
        //print(test);
        //ovfsManager.close();
        handler.create_resource("/darwin", "0744", "litiales", "litiales", Functions.FILE_NOD, true);
        byte[] buffer = null;
        try {
            buffer = Files.readAllBytes(Paths.get("/home/litiales/darwin.iso"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer2 = new byte[buffer.length];
        //long t1 = System.currentTimeMillis();
        //handler.write("/darwin", buffer, 0, buffer.length, "litiales", "litiales");
        long t2 = System.currentTimeMillis();
        buffer2 = handler.read("/darwin", 0, buffer.length, "litiales", "litiales", ret);
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < buffer.length; i++)
            if (buffer[i] != buffer2[i])
                System.out.println("err");
        System.out.println(t3 - t2);
        //System.out.println(t2 - t1);
        System.out.println(buffer.length);
        //ovfsManager.drop();
        //print(buffer);
    }

    public static void print(byte[] test) {
        for (byte item : test)
            System.out.print(item);
        System.out.println();
    }

}
