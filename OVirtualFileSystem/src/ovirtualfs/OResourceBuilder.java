package ovirtualfs;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/13/13
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */

class OResourceBuilder {

    private OrientGraph graph;
    private static final int                    NO_ERROR = 0;
    private static final int                    NOT_A_DIRECTORY_ERROR = -2;
    private static final int                    FILE_OR_DIRECTORY_EXISTS = -3;
    static final String                         DIRECTORY = "Directory";
    static final String                         FILE = "File";
    static final String                         LINK = "Link";

    OResourceBuilder (OrientGraph graph) {
        this.graph = graph;
    }

    int createResource (OrientVertex directory, String resourceName, String type) {
        if (!directory.getLabel().equals("Directory"))
            return NOT_A_DIRECTORY_ERROR;
        if (directory.getVertices(Direction.OUT, resourceName).iterator().hasNext())
            return FILE_OR_DIRECTORY_EXISTS;
        OrientVertex newResource;
        if (!type.equals(DIRECTORY) && !type.equals(FILE) && !type.equals(LINK))
            type = DIRECTORY;
        newResource = graph.addVertex("class:" + type);
        newResource.setProperty("name", resourceName);
        newResource.setProperty("permissions", null);
    }

}
