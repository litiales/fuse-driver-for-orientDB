package OVFSOptimization;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.concurrent.ConcurrentHashMap;

public class OVFSCacheL1 {

    private static OVFSCacheL1 cacheL1Instance;
    private ConcurrentHashMap<OIdentifiable, ODocument> cacheL1;

    private OVFSCacheL1() {
        cacheL1 = new ConcurrentHashMap<OIdentifiable, ODocument>();
    }

    public static OVFSCacheL1 getCacheL1() {
        if (cacheL1Instance == null)
            cacheL1Instance = new OVFSCacheL1();
        return cacheL1Instance;
    }

    public ODocument tryAndGet(OIdentifiable oid) {
        ODocument currentNode = null;
        if (!cacheL1.containsKey(oid)) {
            currentNode = oid.getRecord();
            cacheL1.put(oid, currentNode);
        } else {
            currentNode = cacheL1.get(oid);
        }
        return currentNode;
    }

}
