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
package de.hsmainz.cs.semgis.arqextension.polygon;

import java.util.List;

import org.geotools.geometry.jts.GeometryBuilder;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

import de.hsmainz.cs.semgis.arqextension.DoubleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;
import de.hsmainz.cs.semgis.arqextension.datatypes.WKTLiteral;

public class MakePolygon extends DoubleGeometrySpatialFunction {

	@Override
	protected String[] getRestOfArgumentTypes() {
		 return new String[] { };
	}

	@Override
	protected NodeValue exec(Geometry g1, Geometry g2, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		GeometryBuilder builder = new GeometryBuilder();
		return SpatialFunctionBase.makeNodeValue(builder.polygon((LinearRing)g1, (LinearRing)g2),new WKTLiteral());
	}

}
