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
package de.hsmainz.cs.semgis.arqextension.envelope;

import de.hsmainz.cs.semgis.arqextension.SingleGeometrySpatialFunction;
import de.hsmainz.cs.semgis.arqextension.datatypes.GeoSPARQLLiteral;
import java.util.List;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionEnv;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

public abstract class SingleEnvelopeSpatialFunction extends SingleGeometrySpatialFunction {

    @Override
    protected NodeValue exec(Geometry g, GeoSPARQLLiteral datatype, Binding binding, List<NodeValue> evalArgs,
            String uri, FunctionEnv env) {
        NodeValue geom = evalArgs.get(0);
        checkGeometryLiteral(geom);

        Envelope point = (Envelope) geom.getNode().getLiteralValue();
        return exec(point, datatype, binding, evalArgs, uri, env);
    }

    protected abstract NodeValue exec(Envelope g, GeoSPARQLLiteral datatype, Binding binding,
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
}
