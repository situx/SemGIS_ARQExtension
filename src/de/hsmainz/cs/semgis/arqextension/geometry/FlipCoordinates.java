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

import de.hsmainz.cs.semgis.arqextension.SingleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class FlipCoordinates extends SingleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g, GeoSPARQLLiteral datatype, Binding binding, List<NodeValue> evalArgs,
			String uri, FunctionEnv env) {
		Coordinate[] original =g.getCoordinates();
        for(int i =0; i<original.length; i++){
            Double swapValue = original[i].x;
            original[i].x = original[i].y;
            original[i].y = swapValue;
        }
        return makeNodeValue(g, datatype);
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

}
