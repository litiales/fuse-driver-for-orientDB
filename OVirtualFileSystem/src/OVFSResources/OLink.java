package OVFSResources;

import OVFSException.OUnknownLinkRef;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.util.List;

public class OLink extends OBaseResource {

    OBaseResource linkedResource;

    public OLink(String resourceName, OIdentifiable currRecord, ODirectory ancestor, OGraphDatabase databaseReference, OBaseResource linkedResource) {
        super(resourceName, currRecord, ancestor, databaseReference);
        this.linkedResource = linkedResource;
    }

    public void ls(String path) {
        System.out.println(path + "/" + resourceName);
    }

    public List<OBaseResource> getResource() {
        if (linkedResource != null)
            return linkedResource.getResource();
        else
            return null;
    }

    public List<OBaseResource> getLinkedResource() throws OUnknownLinkRef {
        List<OBaseResource> linked;
        if (linkedResource == null || (linked = linkedResource.getResource()) == null)
            throw new OUnknownLinkRef();
        return linked;
    }

}
