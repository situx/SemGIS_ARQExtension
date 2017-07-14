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
import com.hp.hpl.jena.vocabulary.XSD;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import de.hsmainz.cs.semgis.arqextension.SingleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class DelaunayTriangles extends SingleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g, GeoSPARQLLiteral datatype, Binding binding, List<NodeValue> evalArgs,
			String uri, FunctionEnv env) {
		DelaunayTriangulationBuilder builder=new DelaunayTriangulationBuilder();
		builder.setTolerance(evalArgs.get(0).getDouble());
		return makeNodeValue(builder.getTriangles(new GeometryFactory()), datatype);
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{XSD.xdouble.getURI(),XSD.xint.getURI()};
	}

}
