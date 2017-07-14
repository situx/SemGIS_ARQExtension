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
package de.hsmainz.cs.semgis.arqextension.point;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import de.hsmainz.cs.semgis.arqextension.DoubleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class Azimuth extends DoubleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g1, Geometry g2, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		GeodeticCalculator calc=new GeodeticCalculator();

		if(g1 instanceof Point && g2 instanceof Point){
			Point2D point1=new java.awt.Point();
			point1.setLocation(g1.getCoordinate().x, g1.getCoordinate().y);
			Point2D point2=new java.awt.Point();
			point2.setLocation(g2.getCoordinate().x, g2.getCoordinate().y);
			calc.setStartingGeographicPoint(point1);
			calc.setDestinationGeographicPoint(point2);
			return NodeValue.makeDouble(calc.getAzimuth());
		}
		// TODO Auto-generated method stub
		return NodeValue.nvNothing;
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{};
	}


}
