package OVFSResources;

import OVFSException.ODuplicatedFileName;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.util.ArrayList;
import java.util.List;

public class ODirectory extends OBaseResource {

    private OResourcesHashMap subHashMap;

    public ODirectory(String resourceName, OIdentifiable currRecord, ODirectory ancestor, OGraphDatabase databaseReference) {
        super(resourceName, currRecord, ancestor, databaseReference);
        subHashMap = new OResourcesHashMap();
    }

    public boolean addSubResource(String newResName, OBaseResource newResource) throws ODuplicatedFileName {
        if (!subHashMap.addSubResource(newResName, newResource)) //c'è un errore
            throw new ODuplicatedFileName(newResName);
        //ODocument currNode = getODocumentFromOID(currID);
        //databaseReference.createEdge(currNode, newResource.getODocument()).save(); //creo un collegamento fra vecchio e nuovo nodo;
        return true;
    }

    boolean modifyFileHashMap(String oldFileName, String newFileName, OFile nodeToBeModified) {
        return subHashMap.modifyHashMap(oldFileName, newFileName, nodeToBeModified);
    }

    public void ls(String path) {
        ArrayList<OBaseResource> resourceSet = subHashMap.getSortedResources();
        if (resourceSet == null) { //è una foglia
            System.out.println(path + "/" + resourceName + "/");
            return;
        }
        for (OBaseResource iterator : resourceSet) {
            iterator.ls(path + "/" + this.resourceName);
        }
    }

    public List<OBaseResource> getResource() {
        return subHashMap.getSortedResources();
    }

}