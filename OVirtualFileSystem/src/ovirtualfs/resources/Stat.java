package ovirtualfs.resources;

import java.util.Date;

public class Stat {

    public String mode;
    public String uid;
    public String gid;
    public int size;
    public Date atime;
    public Date mtime;
    public Date ctime;
    public String type;

    public Stat(String mode, String uid, String gid, int size, Date atime, Date mtime, Date ctime, String type) { //e' sufficiente un solo time in quanto il costruttore
        this.mode = mode;                                                  // viene richiamato solo all'atto della creazione del nodo
        this.uid = uid;
        this.gid = gid;
        this.size = size;
        this.atime = atime;
        this.ctime = ctime;
        this.mtime = mtime;
        this.type = type;
    }

}
