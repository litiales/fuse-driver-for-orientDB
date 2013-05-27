package ovirtualfs;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: litiales
 * Date: 5/6/13
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */

class ODatabaseBrowser {

    private OrientGraph graphDatabase;
    private OrientVertex root;
    private String pwdString;


    ODatabaseBrowser (OrientGraph graphDatabase, OrientVertex root) {
        this.graphDatabase = graphDatabase;
        this.root = root;
        pwdString = "/";
    }

    void deepLs (Vertex currRoot, String currPath) {
        if (((OrientVertex) currRoot).getLabel().equals("Link")) {
            System.out.println(((OrientVertex) currRoot).getLabel() + " " + currPath + currRoot.getProperty("name"));
            return;
        }
        Iterator<Vertex> iterator =  currRoot.getVertices(Direction.OUT).iterator();
        if (iterator.hasNext()) {
            while (iterator.hasNext())
                deepLs(iterator.next(), currPath + currRoot.getProperty("name"));
        } else
            System.out.println(((OrientVertex) currRoot).getLabel() + " " + currPath + currRoot.getProperty("name"));
    }

    OrientVertex getResourcePath(String path) {}

}
