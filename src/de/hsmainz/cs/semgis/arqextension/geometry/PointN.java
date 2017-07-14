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

import java.math.BigInteger;
import java.util.List;

import org.geotools.geometry.jts.GeometryBuilder;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.vocabulary.XSD;
import com.vividsolutions.jts.geom.Geometry;

import de.hsmainz.cs.semgis.arqextension.SingleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class PointN extends SingleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g, GeoSPARQLLiteral datatype, Binding binding, List<NodeValue> evalArgs,
			String uri, FunctionEnv env) {
		GeometryBuilder builder = new GeometryBuilder();
		BigInteger n=evalArgs.get(0).getInteger();
		if(n.intValue()>=g.getCoordinates().length){
			return NodeValue.nvNothing;
		}
		return 	makeNodeValue(builder.point(g.getCoordinates()[n.intValue()].x, g.getCoordinates()[n.intValue()].y), datatype);

	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{XSD.xint.getURI()};
	}

}
