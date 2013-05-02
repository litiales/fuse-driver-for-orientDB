package OVFSResources;

import OVFSOptimization.OVFSCacheL1;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;

import java.util.concurrent.ConcurrentHashMap;

public class OFile {

    private String fileName;
    private OIdentifiable currRecord;
    private OIdentifiable ancestor;
    private ConcurrentHashMap<OIdentifiable, ORecord> cacheL1;
    private OGraphDatabase graphDatabase;

    public OFile(String fileName, OIdentifiable currRecord, OIdentifiable ancestor){
        cacheL1 = OVFSCacheL1.getL1Cache();
        fileName = fileName;
        currRecord = currRecord;
        ancestor = ancestor;
    }

    String getFileName(){
        return fileName;
    }

    String changeFileName(String newFileName){
        ORecord currNode;
        currNode = retriveNodeByOID(currRecord);
    }

    private ORecord retriveNodeByOID(OIdentifiable oid){
        if (!cacheL1.containsKey(oid)) {
            cacheL1.put(oid, graphDatabase.)
        }

    }

}
