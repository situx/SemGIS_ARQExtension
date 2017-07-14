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
package de.hsmainz.cs.semgis.arqextension.raster;

import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.coverage.Coverage;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Geometry;

import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public abstract class Raster2DGeometrySpatialFunction extends SpatialFunctionBase {

	   /**
	    * {@inheritDoc}
	    */
	   @Override
	   protected NodeValue exec(Binding binding, List<NodeValue> evalArgs,
	         String uri, FunctionEnv env) {
	      NodeValue geom = evalArgs.get(0);
	      GridCoverage2D raster=(GridCoverage2D)geom.getNode().getLiteralValue();
	      geom=evalArgs.get(1);
	      checkGeometryLiteral(geom);
	      Geometry g = (Geometry) geom.getNode().getLiteralValue();
	      GeoSPARQLLiteral datatype = (GeoSPARQLLiteral)geom.getNode().getLiteralDatatype();
	      return exec(raster,g, datatype, binding, evalArgs, uri, env);
	   }

	   protected abstract NodeValue exec(GridCoverage2D raster,Geometry g, GeoSPARQLLiteral datatype, Binding binding,
	         List<NodeValue> evalArgs, String uri, FunctionEnv env);

	   protected abstract String[] getRestOfArgumentTypes();

	   /**
	    * {@inheritDoc}
	    */
	   @Override
	   protected String[] getArgumentTypes() {
	      String[] rest = getRestOfArgumentTypes();
	      String[] args = new String[rest.length + 1];
	      args[0] = "ogc:GeomLiteral";
	      int i = 1;
	      for (String s : rest) {
	         args[i++] = s;
	      }
	      return args;
	   }
	   
	   public static NodeValue makeNodeValueRaster(Coverage coverage,GeoSPARQLLiteral datatype){
		      return NodeValue.makeNode("ogc:Raster", null, datatype.getURI());
	   }

}
