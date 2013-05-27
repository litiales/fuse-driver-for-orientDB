package com.tinkerpop.blueprints.impls.orient;

import java.util.Iterator;

import com.orientechnologies.common.util.OPair;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.iterator.OLazyWrapperIterator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

public class OrientVertexIterator extends OLazyWrapperIterator<Vertex> {
  private final OrientVertex             vertex;
  private final String[]                 iLabels;
  private final OPair<Direction, String> connection;

  public OrientVertexIterator(final OrientVertex orientVertex, final Iterator<?> iterator,
      final OPair<Direction, String> connection, final String[] iLabels) {
    super(iterator);
    this.vertex = orientVertex;
    this.connection = connection;
    this.iLabels = iLabels;
  }

  @Override
  public Vertex createWrapper(final Object iObject) {
    if (iObject instanceof OrientVertex)
      return (OrientVertex) iObject;

    final ODocument value = ((OIdentifiable) iObject).getRecord();
    final OrientVertex v;
    if (value.getSchemaClass().isSubClassOf(OrientVertex.CLASS_NAME)) {
      // DIRECT VERTEX
      v = new OrientVertex(vertex.graph, value);
    } else if (value.getSchemaClass().isSubClassOf(OrientEdge.CLASS_NAME)) {
      // EDGE
      if (vertex.graph.isUseVertexFieldsForEdgeLabels() || OrientEdge.isLabeled(OrientEdge.getRecordLabel(value), iLabels))
        v = new OrientVertex(vertex.graph, OrientEdge.getConnection(value, connection.getKey().opposite()));
      else
        v = null;
    } else
      throw new IllegalStateException("Invalid content found between connections:" + value);

    return v;
  }

  public boolean filter(final Vertex iObject) {
    return true;
  }
}