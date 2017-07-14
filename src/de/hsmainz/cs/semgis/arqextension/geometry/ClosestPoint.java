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

import java.util.List;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import de.hsmainz.cs.semgis.arqextension.DoubleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class ClosestPoint extends DoubleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g1, Geometry g2, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		GeometryFactory fact=new GeometryFactory();
		DistanceOp distop=new DistanceOp(g1,g2);
		Coordinate[] coords=distop.nearestPoints();
		Coordinate coord=new Coordinate(coords[0].x,coords[0].y);
		Point point=fact.createPoint(coord);		
		return makeNodeValue(point,datatype);
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

}
