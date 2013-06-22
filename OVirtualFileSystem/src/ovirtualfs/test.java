package ovirtualfs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {

    public static void main(String args[]) {

        try {
            System.setErr(new PrintStream(new FileOutputStream("/dev/null")));
        } catch (FileNotFoundException e) {
            return;
        }

        if (args.length == 0){
            System.out.println("No argoument specified");
            return;
        }
        String op = args[0];
        String path = null;
        int blockSize = 0;
        if (args.length>1){
            path = args[1];
        }
        if (args.length>2){
            blockSize = Integer.parseInt(args[2]);
        }

        OVFSManager ovfsManager = OVFSManager.getOVFSHandler("./", "test", blockSize);
        Functions handler = ovfsManager.getFunctionHandler();
        IntWrapper ret = new IntWrapper();

        if (op.contains("w")){
            if (path == null) {
                System.out.println("No file specified");
                return;
            }
            byte[] buffer = null;
            try {
                buffer = Files.readAllBytes(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
                ovfsManager.drop();
                return;
            }
            handler.create_resource("/file", "0744", "litiales", "litiales", Functions.FILE_NOD, true);
            long t1 = System.currentTimeMillis();
            handler.write("/file", buffer, 0, buffer.length, "root", "root");
            long t2 = System.currentTimeMillis();
            System.out.println("Writing time of " + buffer.length + " bytes: " + (t2-t1));
            if (op.equals("w")){
                ovfsManager.drop();
                return;
            }
        }
        if (op.contains("r")) {
            int size = handler.getattr("/file", ret, "root", "root").getSize();
            byte[] buffer2 = new byte[size];
            long t3 = System.currentTimeMillis();
            buffer2 = handler.read("/file", 0, size, "root", "root", ret);
            long t4 = System.currentTimeMillis();
            System.out.println("Reading time of " + buffer2.length + " bytes: " + (t4 - t3));
        }

        ovfsManager.close();
    }
}
