package OVFSResources;

import OVFSOptimization.OVFSCacheL1;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;

public abstract class OBaseResource {

    protected OIdentifiable currID;
    protected String resourceName;
    protected OVFSCacheL1 cacheL1;
    protected ODirectory ancestor;
    protected OGraphDatabase databaseReference;

    public OBaseResource (String resourceName, OIdentifiable currRecord, ODirectory ancestor, OGraphDatabase databaseReference) {
        this.cacheL1 = OVFSCacheL1.getCacheL1();
        this.resourceName = resourceName;
        this.currID = currRecord;
        this.ancestor = ancestor;
        this.databaseReference = databaseReference;
    }

    public String getFileName() {
        return resourceName;
    }

    abstract List<OBaseResource> getResource();

    protected ODocument getODocumentFromOID (OIdentifiable oid) {
        return (ODocument) oid.getRecord();
    }

    protected ODocument getODocument() {
        return getODocumentFromOID(currID);
    }

    public abstract void ls(String path);

}
