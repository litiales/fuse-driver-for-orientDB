package OVFSResources;

import OVFSException.ODuplicatedFileName;
import OVFSOptimization.OVFSCacheL1;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.concurrent.ConcurrentHashMap;

public class OFile {

    private String fileName;
    private OIdentifiable currRecord;
    private ODirectory ancestor;
    private ConcurrentHashMap<OIdentifiable, ODocument> cacheL1;
    private OGraphDatabase graphDatabase;

    public OFile(String fileName, OIdentifiable currRecord, OIdentifiable ancestorID) {
        cacheL1 = OVFSCacheL1.getL1Cache();
        fileName = fileName;
        currRecord = currRecord;
        ancestorID = ancestorID;
    }

    String getFileName() {
        return fileName;
    }

    void changeFileName(String newFileName) throws ODuplicatedFileName {
        if (!ancestor.modifyFileHashMap(fileName, newFileName, this))
            throw new ODuplicatedFileName();
        ODocument currNode;
        currNode = retriveNodeByOID(currRecord);
        currNode.field("filename", newFileName);
        currNode.save();
        fileName = newFileName;
    }

    private ODocument retriveNodeByOID(OIdentifiable oid) {
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
