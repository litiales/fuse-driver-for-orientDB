package OVFSResources;

import com.orientechnologies.orient.core.db.record.OIdentifiable;

public class ODirectory extends OBaseResource {

    private OResourcesHashMap subHashMap;

    public ODirectory(String resourceName, OIdentifiable currRecord, ODirectory ancestor) {
        super(resourceName, currRecord, ancestor);
        subHashMap = new OResourcesHashMap();
    }

    boolean modifyFileHashMap(String oldFileName, String newFileName, OFile nodeToBeModified) {
        return subHashMap.modifyHashMap(oldFileName, newFileName, nodeToBeModified);
    }

}
