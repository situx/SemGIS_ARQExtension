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
import org.geotools.coverage.grid.GridCoverageFactory;


import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.vocabulary.XSD;

import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class MakeEmptyRaster extends RasterSpatialFunction {

	@Override
	protected NodeValue exec(GridCoverage2D raster, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		Integer width;
		Integer height;
		Double upperleftx;
		Double upperlefty;
		GridCoverageFactory factory=new GridCoverageFactory();
		if(raster!=null){
			//factory.
		}else{
			width=evalArgs.get(0).getInteger().intValue();
			height=evalArgs.get(1).getInteger().intValue();
			upperleftx=evalArgs.get(2).getDouble();
			upperlefty=evalArgs.get(3).getDouble();
		}
		return null;
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{XSD.xint.getURI(),XSD.xint.getURI(),XSD.xdouble.getURI(),XSD.xdouble.getURI(),XSD.xdouble.getURI()};
	}

}
