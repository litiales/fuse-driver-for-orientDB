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

public class ODatabaseBrowser {

    private OrientGraph graphDatabase;
    private OrientVertex root;
    private String pwdString;


    ODatabaseBrowser (OrientGraph graphDatabase, OrientVertex root) {
        this.graphDatabase = graphDatabase;
        this.root = root;
        pwdString = "/";
    }

    public void deepLs (Vertex currRoot, String currPath) {
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

    public OrientVertex getResourcePath(String path) {

        String[] canonicalPath = path.split("/");
        int pathLenght = canonicalPath.length;

        if (pathLenght == 2) { // siamo direttamente sotto la root
            if (root.countEdges(Direction.OUT, canonicalPath[1] + "/") == 0 && root.countEdges(Direction.OUT, canonicalPath[1]) == 0)
                return root;
            else {
                System.out.println("[Error] mknod requested for path " + path + " but it already exists");
                return null;
            }
        }

        OrientVertex electedVertex = root;
        int index = 1; //l'elemento 0 è quello che si ottiene dallo split prima dello slash
        Iterator<Vertex> iterator;

        //TODO sistemare la funzione

        while (true){

            System.out.println(electedVertex);

            if (pathLenght == index + 1){ //Sono all'ultimo livello
                System.out.println("entrato");
                if (electedVertex.countEdges(Direction.OUT, canonicalPath[index] + "/") != 0 && electedVertex.countEdges(Direction.OUT, canonicalPath[index]) == 0) { //esiste già una risorsa con questo nome
                    System.out.println("[Error] mknod requested for path " + path + " but it already exists");
                    return null;
                }
                else //non esiste una risorsa
                    return electedVertex;
            }

            iterator = electedVertex.getVertices(Direction.OUT, canonicalPath[index]).iterator();
            electedVertex = iterator.hasNext() ? (OrientVertex) iterator.next() : null;

            if (electedVertex == null) { //non c'è alcun nodo intermedio
                System.out.println("[Error] mknod requested for path " + path + " but no souch directory exists");
                return null;
            }

            index++;

        }
    }

}
