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
package de.hsmainz.cs.semgis.arqextension.linestring;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.vocabulary.XSD;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.hsmainz.cs.semgis.arqextension.SingleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;
import de.hsmainz.cs.semgis.arqextension.geometry.SingleToManyGeometrySpatialFunction;

public class Segmentize extends SingleGeometrySpatialFunction {

	
	public List<Geometry> createSegments(Geometry track, double segmentLength) throws NoSuchAuthorityCodeException, 
    FactoryException {

    GeodeticCalculator calculator = new GeodeticCalculator(CRS.decode("EPSG:4326")); // KML uses WGS84
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
    
    LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>();
    Collections.addAll(coordinates, track.getCoordinates());
    
    double accumulatedLength = 0;
    List<Coordinate> lastSegment = new ArrayList<Coordinate>();
    List<Geometry> segments = new ArrayList<Geometry>();
    Iterator<Coordinate> itCoordinates = coordinates.iterator();
    
    for (int i = 0; itCoordinates.hasNext() && i < coordinates.size() - 1; i++) {
        Coordinate c1 = coordinates.get(i);
        Coordinate c2 = coordinates.get(i + 1);
        
        lastSegment.add(c1);
        
        calculator.setStartingGeographicPoint(c1.x, c1.y);
        calculator.setDestinationGeographicPoint(c2.x, c2.y);
        
        double length = calculator.getOrthodromicDistance();
        
        if (length + accumulatedLength >= segmentLength) {
            double offsetLength = segmentLength - accumulatedLength;
            double ratio = offsetLength / length;
            double dx = c2.x - c1.x;
            double dy = c2.y - c1.y;
            
            Coordinate segmentationPoint = new Coordinate(c1.x + (dx * ratio), 
                                                          c1.y + (dy * ratio)); 
            
            lastSegment.add(segmentationPoint); // Last point of the segment is the segmentation point
            segments.add(geometryFactory.createLineString(lastSegment.toArray(new Coordinate[lastSegment.size()])));
            
            lastSegment = new ArrayList<Coordinate>(); // Resets the variable since a new segment will be built
            accumulatedLength = 0D;
            coordinates.add(i + 1, segmentationPoint); 
        } else {
            accumulatedLength += length;
        }
    }
    
    lastSegment.add(coordinates.getLast()); // Because the last one is never added in the loop above
    segments.add(geometryFactory.createLineString(lastSegment.toArray(new Coordinate[lastSegment.size()])));
    
    return segments;
}

	@Override
	protected NodeValue exec(Geometry g, GeoSPARQLLiteral datatype, Binding binding, List<NodeValue> evalArgs,
			String uri, FunctionEnv env) {
		try {
			return SingleToManyGeometrySpatialFunction.makeNodeValueList(this.createSegments(g, evalArgs.get(0).getDouble()),datatype);
		} catch (FactoryException e) {
			return NodeValue.nvNothing;
		}
		
	}

	@Override
	protected String[] getRestOfArgumentTypes() {
		// TODO Auto-generated method stub
		return new String[]{XSD.xdouble.getURI()};
	}
}
