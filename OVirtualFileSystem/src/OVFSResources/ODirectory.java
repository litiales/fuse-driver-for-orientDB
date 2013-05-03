package OVFSResources;

public class ODirectory extends OBaseResource {

    private OResourcesHashMap subHashMap;

    boolean modifyFileHashMap(String oldFileName, String newFileName, OFile nodeToBeModified) {
        return subHashMap.modifyHashMap(oldFileName, newFileName, nodeToBeModified);
    }

}
