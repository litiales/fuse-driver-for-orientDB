package ovirtualfs;

public class DirList {

    private String resName;
    private String resType;

    DirList(String resName, String resType) {
        this.resName = resName;
        this.resType = resType;
    }

    public String getResName() {
        return resName;
    }

    public String getResType() {
        return resType;
    }

}
