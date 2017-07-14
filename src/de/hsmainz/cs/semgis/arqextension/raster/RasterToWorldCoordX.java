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

import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.TransformException;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;

import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class RasterToWorldCoordX extends RasterSpatialFunction {

	@Override
	protected NodeValue exec(GridCoverage2D raster, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		Integer column=evalArgs.get(0).getInteger().intValue();
		Integer row=evalArgs.get(1).getInteger().intValue();		
		try {
			DirectPosition position=raster.getGridGeometry().gridToWorld(new GridCoordinates2D(column,row));
			return NodeValue.makeDouble(position.getCoordinate()[0]);
		} catch (TransformException e) {
			return NodeValue.nvNothing;
		}
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

}
