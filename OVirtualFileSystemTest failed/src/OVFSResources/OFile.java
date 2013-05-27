package OVFSResources;

import OVFSException.ODuplicatedFileName;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;

public class OFile extends OBaseResource {

    private OGraphDatabase graphDatabase;

    public OFile(String resourceName, OIdentifiable currRecord, ODirectory ancestor, OGraphDatabase databaseRefernce) {
        super(resourceName, currRecord, ancestor, databaseRefernce);
    }

    void changeFileName(String newFileName) throws ODuplicatedFileName {
        if (!ancestor.modifyFileHashMap(resourceName, newFileName, this)) //c'è già una risorsa con il mio nome
            throw new ODuplicatedFileName(newFileName);
        ODocument currNode;
        currNode = retriveNodeByOID(currID);
        currNode.field("filename", newFileName);
        currNode.save();
        resourceName = newFileName;
    }

    private ODocument retriveNodeByOID(OIdentifiable oid) {
        return cacheL1.tryAndGet(oid);
    }

    public void ls(String path) {
        System.out.println(path + "/" + resourceName);
    }

    public List<OBaseResource> getResource() {
        List<OBaseResource> ret;
        ret = new ArrayList<OBaseResource>(1);
        ret.add(this);
        return ret;
    }

}
