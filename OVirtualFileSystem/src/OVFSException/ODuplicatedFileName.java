package OVFSException;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/2/13
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ODuplicatedFileName extends RuntimeException {

    public ODuplicatedFileName(String resName){
        super("The name \"" + resName + "\" is already used in this location. Please use a different name.");
    }

}
