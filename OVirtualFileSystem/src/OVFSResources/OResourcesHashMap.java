package OVFSResources;

import java.util.*;

class OResourcesHashMap {

    private HashMap<String, OBaseResource> resourceHashMap;

    public OResourcesHashMap(){
        resourceHashMap = new HashMap<String, OBaseResource>();
    }

    boolean addSubResource (String resName, OBaseResource resource) {
        if (resourceHashMap.containsKey(resName))
            return false;
        resourceHashMap.put(resName, resource);
        return true;
    }

    boolean modifyHashMap(String oldResName, String newResName, OFile nodeToBeModified) {
        if (resourceHashMap.containsKey(newResName))
            return false;
        if (resourceHashMap.containsKey(oldResName)) {
            resourceHashMap.remove(oldResName);
        }
        resourceHashMap.put(newResName, nodeToBeModified);
        return true;
    }

    ArrayList<OBaseResource> getSortedResources() {
        TreeSet<String> treeKeys = new TreeSet<String>();
        for (String key : resourceHashMap.keySet())
            treeKeys.add(key);
        ArrayList<OBaseResource> resourceLinkedList = new ArrayList<OBaseResource>(resourceHashMap.size());
        for (String key : treeKeys)
            resourceLinkedList.add(resourceHashMap.get(key));
        return resourceLinkedList;
    }

}
