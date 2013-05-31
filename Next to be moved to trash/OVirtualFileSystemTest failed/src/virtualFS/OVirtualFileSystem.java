package virtualFS;

import OVFSResources.ODirectory;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

public class OVirtualFileSystem {

    private OGraphDatabase oGraphDatabase;
    private ODirectory root;

    public OVirtualFileSystem(String iURL) throws RuntimeException{
        try {
            oGraphDatabase = new OGraphDatabase("local:" + iURL);
        } catch (RuntimeException re) {
            throw new RuntimeException(); //check which exception is raised when iURL not exists
        }
    }

    public void openFS(){
        oGraphDatabase.open("admin", "admin");
    }

    public void openFS(String iUser, String iPassword) {
        oGraphDatabase.open(iUser, iPassword);
    }

    public void create(){
        oGraphDatabase.create();
    }

    public void createAndOpen(){
        create();
        openFS();
    }

}
