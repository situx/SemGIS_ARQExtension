package de.hsmainz.cs.semgis.arqextension.point;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import com.github.davidmoten.geo.LatLong;

import io.github.galbiston.geosparql_jena.implementation.GeometryWrapper;
import io.github.galbiston.geosparql_jena.implementation.datatype.WKTDatatype;
import io.github.galbiston.geosparql_jena.implementation.vocabulary.SRS_URI;

public class PointFromGeoHash extends FunctionBase1 {


	    @Override
	    public NodeValue exec(NodeValue arg0) {

	        try {
	            String geohash = arg0.getString();
	            LatLong latlng=com.github.davidmoten.geo.GeoHash.decodeHash(geohash);
	            Coordinate coord=new Coordinate();
	            coord.x=latlng.getLat();
	            coord.y=latlng.getLon();
	            GeometryWrapper pointWrapper = GeometryWrapper.createPoint(coord, SRS_URI.DEFAULT_WKT_CRS84, WKTDatatype.URI);	            
	            return pointWrapper.asNodeValue();
	        } catch (DatatypeFormatException ex) {
	            throw new ExprEvalException(ex.getMessage(), ex);
	        }
	    }

}
