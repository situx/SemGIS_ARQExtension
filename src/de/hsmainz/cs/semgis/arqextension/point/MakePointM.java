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

import org.geotools.geometry.jts.GeometryBuilder;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase3;

import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.WKTLiteral;

public class MakePointM extends FunctionBase3 {

	@Override
	public NodeValue exec(NodeValue arg0, NodeValue arg1, NodeValue arg2) {
		GeometryBuilder builder = new GeometryBuilder();
		return SpatialFunctionBase.makeNodeValue(builder.pointZ(arg0.getDouble(), arg1.getDouble(),arg2.getDouble()), new WKTLiteral());
	}

}
