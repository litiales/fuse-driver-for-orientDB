package com.tinkerpop.blueprints.impls.orient;

import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * A Blueprints implementation of the graph database OrientDB (http://www.orientechnologies.com)
 * 
 * @author Luca Garulli (http://www.orientechnologies.com)
 */
class OrientGraphContext {
  public Map<String, OrientIndex<? extends OrientElement>> manualIndices = new HashMap<String, OrientIndex<? extends OrientElement>>();
  public ODatabaseDocumentTx                               rawGraph;
}