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

import org.apache.jena.graph.Node;

public class UnsupportedUnitsException extends GeoSPARQLFunctionException {

    private static final long serialVersionUID = -8703059733315979561L;

    public UnsupportedUnitsException(Node units) {
        super(String.format("%s is not supported for this function", units));
    }

}
