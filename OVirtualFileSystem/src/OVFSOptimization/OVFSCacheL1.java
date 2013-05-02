package OVFSOptimization;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/1/13
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class OVFSCacheL1 {

    private static ConcurrentHashMap<OIdentifiable, ORecord> cacheL1;

    public static ConcurrentHashMap getL1Cache(){
        if (cacheL1 == null)
            cacheL1 = new ConcurrentHashMap<OIdentifiable, ORecord>(10);
        return cacheL1;
    }

}
