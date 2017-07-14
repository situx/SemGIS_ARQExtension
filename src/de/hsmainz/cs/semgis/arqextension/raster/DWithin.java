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

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.GeodeticCalculator;


import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.vocabulary.XSD;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class DWithin extends DoubleRaster2DSpatialFunction {
	   @Override
	   protected NodeValue exec(GridCoverage2D raster,GridCoverage2D raster2, GeoSPARQLLiteral datatype, Binding binding,
		         List<NodeValue> evalArgs, String uri, FunctionEnv env){
		   Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
		   Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
		   Double withinDistance=evalArgs.get(0).getDouble();
		   GeodeticCalculator calc=new GeodeticCalculator();
		   Coordinate[] points = DistanceOp.nearestPoints(JTS.toGeometry(bbox1.getBounds()), JTS.toGeometry(bbox2.getBounds()));
           calc.setStartingGeographicPoint(points[0].x, points[0].y);
           calc.setDestinationGeographicPoint(points[1].x, points[1].y);
           Double actualdistance = calc.getOrthodromicDistance();
           return null;
		   
	   }
	@Override
	protected String[] getRestOfArgumentTypes(){
			   return new String[]{XSD.xdouble.getURI()};
}
	
}
