package ovirtualfs;

import java.util.Date;

public final class Stat {

    private String mode;
    private String uid;
    private String gid;
    private int size;
    private Date atime;
    private Date mtime;
    private Date ctime;
    private String type;

    Stat(String mode, String uid, String gid, int size, Date atime, Date mtime, Date ctime, String type) { //e' sufficiente un solo time in quanto il costruttore
        this.mode = mode;                                                  // viene richiamato solo all'atto della creazione del nodo
        this.uid = uid;
        this.gid = gid;
        this.size = size;
        this.atime = atime;
        this.ctime = ctime;
        this.mtime = mtime;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getMode() {
        return mode;
    }

    public String getUid() {
        return uid;
    }

    public String getGid() {
        return gid;
    }

    public int getSize() {
        return size;
    }

    public Date getAtime() {
        return atime;
    }

    public Date getMtime() {
        return mtime;
    }

    public Date getCtime() {
        return ctime;
    }

}
