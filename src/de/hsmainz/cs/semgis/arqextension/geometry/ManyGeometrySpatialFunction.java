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
package de.hsmainz.cs.semgis.arqextension.geometry;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Geometry;

import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public abstract class ManyGeometrySpatialFunction extends SpatialFunctionBase {

	/**
	    * {@inheritDoc}
	    */
	   @Override
	   protected NodeValue exec(Binding binding, List<NodeValue> evalArgs,
	         String uri, FunctionEnv env) {
	      NodeValue geom1 = evalArgs.get(0);
	      NodeValue geom2 = evalArgs.get(1);
	      List<Geometry> geomlist=new LinkedList<Geometry>();
	      for(NodeValue geom:evalArgs){
	    	  checkGeometryLiteral(geom);
	    	  geomlist.add((Geometry)geom.getNode().getLiteralValue());
	      }

	      GeoSPARQLLiteral datatype = (GeoSPARQLLiteral) geom1.getNode().getLiteralDatatype();

	      return exec(geomlist, datatype, binding, evalArgs, uri, env);
	   }

	   protected abstract NodeValue exec(List<Geometry> geomlist, GeoSPARQLLiteral datatype, Binding binding,
	         List<NodeValue> evalArgs, String uri, FunctionEnv env);


	   protected abstract String[] getRestOfArgumentTypes();

	   /**
	    * {@inheritDoc}
	    */
	   @Override
	   protected String[] getArgumentTypes() {
	      String[] rest = getRestOfArgumentTypes();
	      String[] args = new String[rest.length + 2];
	      args[0] = "ogc:GeomLiteral";
	      args[1] = "ogc:GeomLiteral";
	      int i = 2;
	      for (String s : rest) {
	         args[i++] = s;
	      }
	      return args;
	   }
	
}
