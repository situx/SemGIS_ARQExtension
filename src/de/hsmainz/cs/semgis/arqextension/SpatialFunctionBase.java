/** *****************************************************************************
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
 ****************************************************************************** */
package de.hsmainz.cs.semgis.arqextension;

import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;
import de.hsmainz.cs.semgis.arqextension.vocabulary.GML;
import de.hsmainz.cs.semgis.arqextension.vocabulary.Units;
import de.hsmainz.cs.semgis.arqextension.vocabulary.WKT;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionEnv;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public abstract class SpatialFunctionBase implements Function {

    protected static final TransformCache CACHE = new TransformCache();

    protected static void checkGeometryLiteral(NodeValue nv)
            throws QueryExecException {
        Node node = nv.asNode();
        if (!node.isLiteral() || !(node.getLiteralDatatype() instanceof GeoSPARQLLiteral)) {
            throw new QueryExecException(String.format("%s is not an %s or %s",
                    node.toString(), WKT.WKTLiteral.getURI(), GML.GMLLiteral.getURI()));
        }
    }

    protected static void checkUnits(NodeValue nv) throws QueryExecException {
        Node node = nv.asNode();

        if (Units.Nodes.metre.equals(node)) {
            return;
        }
        if (Units.Nodes.degree.equals(node)) {
            return;
        }
        if (Units.Nodes.GridSpacing.equals(node)) {
            return;
        }
        if (Units.Nodes.radian.equals(node)) {
            return;
        }
        if (Units.Nodes.unity.equals(node)) {
            return;
        }

        throw new UnsupportedUnitsException(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final NodeValue exec(Binding binding, ExprList args, String uri,
            FunctionEnv env) {
        List<NodeValue> evalArgs = new ArrayList<>();
        for (Iterator<Expr> iter = args.iterator(); iter.hasNext();) {
            Expr e = iter.next();
            NodeValue x = e.eval(binding, env);
            evalArgs.add(x);
        }
        return exec(binding, evalArgs, uri, env);
    }

    protected abstract NodeValue exec(Binding binding, List<NodeValue> evalArgs,
            String uri, FunctionEnv env);

    protected abstract String[] getArgumentTypes();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void build(String uri, ExprList args) {
        String[] argTypes = getArgumentTypes();
        int numArgs = argTypes.length;
        if ((null == args && numArgs != 0)
                || (null != args && numArgs != args.size())) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < argTypes.length; i++) {
                String arg = argTypes[i];
                message.append(arg);
                if (i < argTypes.length - 1) {
                    message.append(", ");
                }
            }
            throw new QueryBuildException(
                    String.format("%s requires %d arguments: %s",
                            uri, argTypes.length,
                            message));
        }
    }

    protected static Geometry project(Geometry geometry, String srsCode)
            throws QueryBuildException {
        if (srsCode.equals(geometry.getUserData())) {
            return geometry;
        }

        Geometry projected;
        try {
            // convert to different spatial reference
            CoordinateReferenceSystem source = CRS.decode(geometry.getUserData().toString());
            CoordinateReferenceSystem destination = CRS.decode(srsCode);

            MathTransform transform = CACHE.getTransform(source, destination);
            projected = JTS.transform(geometry, transform);
        } catch (MismatchedDimensionException | FactoryException | TransformException e) {
            throw new QueryBuildException(
                    String.format("Error while project geometry from %s to %s", geometry.getUserData().toString(), srsCode), e);
        }
        return projected;
    }

    public static NodeValue makeNodeValue(Geometry geom,
            GeoSPARQLLiteral datatype) {
        String lex = datatype.unparse(geom);

        return NodeValue.makeNode(lex, null, datatype.getURI());
    }

}
