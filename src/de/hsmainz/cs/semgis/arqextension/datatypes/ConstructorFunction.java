/*******************************************************************************
 * Copyright (c) 2017 Timo Homburg, i3Mainz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD License
 * which accompanies this distribution, and is available at
 * https://directory.fsf.org/wiki/License:BSD_4Clause
 *
 * This project extends work by Ian Simmons who developed the Parliament Triple Store.
 * http://parliament.semwebcentral.org and published his work und BSD License as well.
 *
 *     
 *******************************************************************************/
package de.hsmainz.cs.semgis.arqextension.datatypes;

import java.util.List;

import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.vocabulary.XSD;
import org.locationtech.jts.geom.Geometry;

import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;

public abstract class ConstructorFunction<T extends GeoSPARQLLiteral> extends SpatialFunctionBase {
   private T datatype;

   public ConstructorFunction(T datatype) {
      this.datatype = datatype;
   }

   @Override
   protected String[] getArgumentTypes() {
      return new String[] { XSD.xstring.getURI() };
   }

   @Override
   protected NodeValue exec(Binding binding, List<NodeValue> evalArgs,
         String uri, FunctionEnv env) {
      Geometry g = datatype.parse(evalArgs.get(0).asString());
      return makeNodeValue(g, datatype);
   }
}