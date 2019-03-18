package de.hsmainz.cs.semgis.arqextension.envelope;

import java.math.BigInteger;
import java.util.List;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase;
import org.apache.jena.sparql.function.FunctionBase4;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import io.github.galbiston.geosparql_jena.implementation.GeometryWrapper;
import io.github.galbiston.geosparql_jena.implementation.SRSInfo;
import io.github.galbiston.geosparql_jena.implementation.datatype.WKTDatatype;
import io.github.galbiston.geosparql_jena.spatial.filter_functions.FunctionBase5;

public class MakeEnvelope extends FunctionBase5 {


	@Override
	public NodeValue exec(NodeValue arg0, NodeValue arg1, NodeValue arg2, NodeValue arg3,NodeValue arg4) {
        try {
        	
        	Envelope env=new Envelope();
        	env.init(arg0.getDouble(), arg3.getDouble(), arg1.getDouble(), arg3.getDouble());
        	BigInteger srid=arg4.getInteger();
        	// TODO Create method createEnvelope in GeometryWrapper
            GeometryWrapper envelopeWrapper = GeometryWrapper.createGeometry(env, "<http://www.opengis.net/def/crs/EPSG/0/"+srid+">", WKTDatatype.URI);

            return envelopeWrapper.asNodeValue();
        } catch (DatatypeFormatException | FactoryException | TransformException ex) {
            throw new ExprEvalException(ex.getMessage(), ex);
        }
	}

}