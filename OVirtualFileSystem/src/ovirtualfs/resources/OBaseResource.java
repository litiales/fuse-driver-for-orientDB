package ovirtualfs.resources;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/6/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */

class OBaseResource {

    protected String resName;
    protected int permissions;
    protected String owner;
    protected String group;

    OBaseResource (String resName, String owner, String group) {
        this.resName = resName;
        this.owner = owner;
        this.group = group;
    }

    public int getAttr() {
        return permissions;
    }

    public int settAttr(int permissions) {
        this.permissions = permissions;
    }

}
