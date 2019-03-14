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
package de.hsmainz.cs.semgis.arqextension.datatypes;

import de.hsmainz.cs.semgis.arqextension.Constants;
import de.hsmainz.cs.semgis.arqextension.vocabulary.GML;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.crs.AbstractCRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.ReferenceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rbattle
 */
public class GMLLiteral extends GeoSPARQLLiteral {

    protected static final Logger LOG = LoggerFactory.getLogger(GMLLiteral.class);

    public GMLLiteral() {
        super(GML.GMLLiteral.getURI());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Geometry doParse(String lexicalForm)
            throws DatatypeFormatException {
        try {
            GMLConfiguration configuration = new GMLConfiguration();
            Parser parser = new Parser(configuration);
            Reader input = new StringReader(lexicalForm);
            Geometry g = (Geometry) parser.parse(input);
            if (g.getUserData() instanceof AbstractCRS) {
                AbstractCRS crs = (AbstractCRS) g.getUserData();

                Set<ReferenceIdentifier> identifiers = crs.getIdentifiers();

                ReferenceIdentifier found = null;
                for (ReferenceIdentifier id : identifiers) {
                    if (Citations.identifierMatches(Citations.EPSG, id.getAuthority())) {
                        found = id;
                        break;
                    } else if (Citations.identifierMatches(Citations.CRS, id.getAuthority())) {
                        found = id;
                        break;
                    }
                }
                if (null == found && identifiers.size() > 0) {
                    found = identifiers.iterator().next();
                }
                if (null != found) {
                    g.setUserData(found.toString());
                } else {
                    g.setUserData(Constants.DEFAULT_CRS);
                }
            } else if (null == g.getUserData()) {
                g.setUserData(Constants.DEFAULT_CRS);
            }
            return g;
        } catch (Exception e) {
            LOG.error("Error parsing GML: " + lexicalForm, e);
            throw new DatatypeFormatException(lexicalForm, this, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doUnparse(Geometry geometry) {
        //String iri = getCoordinateReferenceSystemURI((String) geometry.getUserData());

        GMLConfiguration conf = new GMLConfiguration();
        Encoder e = new Encoder(conf);
        QName qname = new QName("http://www.opengis.net/gml", geometry.getGeometryType());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            e.encode(geometry, qname, bos);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String text = new String(bos.toByteArray());
        //GMLWriter writer = new GMLWriter(true);
        //writer.setSrsName(iri);
        //String text = writer.write(geometry);
        return text;
    }

    public static class GMLConstructor extends ConstructorFunction<GMLLiteral> {

        public GMLConstructor() {
            super(new GMLLiteral());
        }
    }

    protected GMLLiteral(String uri) {
        super(uri);
    }

    public static class GMLLiteralGMLNamespace extends GMLLiteral {

        public GMLLiteralGMLNamespace() {
            super(GML.DATATYPE_URI + "gmlLiteral");
        }

        @Override
        public Geometry doParse(String value) {
            LOG.warn("Accepting GML geometry with incorrect datatype gml:gmlLiteral");
            return super.doParse(value);
        }
    }
}
