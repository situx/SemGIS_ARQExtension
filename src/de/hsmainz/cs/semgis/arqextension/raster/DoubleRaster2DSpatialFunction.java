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
package de.hsmainz.cs.semgis.arqextension.raster;

import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;
import java.util.List;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;
import org.geotools.coverage.grid.GridCoverage2D;
import org.locationtech.jts.geom.Geometry;
import org.opengis.coverage.Coverage;

public abstract class DoubleRaster2DSpatialFunction extends SpatialFunctionBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeValue exec(Binding binding, List<NodeValue> evalArgs,
            String uri, FunctionEnv env) {
        NodeValue geom = evalArgs.get(0);
        GridCoverage2D raster = (GridCoverage2D) geom.getNode().getLiteralValue();
        NodeValue geom2 = evalArgs.get(0);
        GridCoverage2D raster2 = (GridCoverage2D) geom2.getNode().getLiteralValue();
        checkGeometryLiteral(geom);
        Geometry g = (Geometry) geom.getNode().getLiteralValue();
        GeoSPARQLLiteral datatype = (GeoSPARQLLiteral) geom.getNode().getLiteralDatatype();
        return exec(raster, raster2, datatype, binding, evalArgs, uri, env);
    }

    protected abstract NodeValue exec(GridCoverage2D raster, GridCoverage2D raster2, GeoSPARQLLiteral datatype, Binding binding,
            List<NodeValue> evalArgs, String uri, FunctionEnv env);

    protected abstract String[] getRestOfArgumentTypes();

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getArgumentTypes() {
        String[] rest = getRestOfArgumentTypes();
        String[] args = new String[rest.length + 1];
        args[0] = "ogc:GeomLiteral";
        int i = 1;
        for (String s : rest) {
            args[i++] = s;
        }
        return args;
    }

    public static NodeValue makeNodeValueRaster(Coverage coverage, GeoSPARQLLiteral datatype) {
        return NodeValue.makeNode("ogc:Raster", null, datatype.getURI());
    }

}
