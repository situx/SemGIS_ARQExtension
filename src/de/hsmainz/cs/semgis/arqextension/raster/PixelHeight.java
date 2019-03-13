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

import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;
import org.locationtech.jts.geom.Geometry;

import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class PixelHeight extends Raster2DGeometrySpatialFunction{

	@Override
	protected NodeValue exec(GridCoverage2D raster, Geometry g, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		return null;
		//return NodeValue.makeDouble(raster.getGridGeometry());
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		return new String[]{};
	}

}
