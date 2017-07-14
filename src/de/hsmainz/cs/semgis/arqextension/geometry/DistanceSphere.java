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

import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import de.hsmainz.cs.semgis.arqextension.DoubleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;

public class DistanceSphere extends DoubleGeometrySpatialFunction {

	@Override
	protected NodeValue exec(Geometry g1, Geometry g2, GeoSPARQLLiteral datatype, Binding binding,
			List<NodeValue> evalArgs, String uri, FunctionEnv env) {
		if(g1 instanceof Point && g2 instanceof Point){
			return NodeValue.makeDouble(calculateDistance(null, new Point[]{(Point)g1,(Point)g2}));
		}else{
			return NodeValue.makeDouble(calculateDistance(null, new Point[]{g1.getInteriorPoint(),g2.getInteriorPoint()}));
		}
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		return new String[]{};
	}
	
	private Double calculateDistance(
            CoordinateReferenceSystem crs, Point[] points) {
    if (crs == null) {
            crs = DefaultGeographicCRS.WGS84;
    }
    double distance = 0.0;
    try {
            distance = JTS.orthodromicDistance(
                            points[0].getCoordinate(),
                            points[1].getCoordinate(), crs);
    } catch (TransformException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
    }

   // Measure<Double, javax.measure.quantity.Length> dist = Measure.valueOf(
              //      distance, SI.METRE);
   // System.out.println(dist.doubleValue(SI.KILOMETRE)
    //                + " Km");
   // System.out.println(dist.doubleValue(NonSI.MILE)
  //                  + " miles");
    return null;//dist.doubleValue(SI.METRE);
}

}
