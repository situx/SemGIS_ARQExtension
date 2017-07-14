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
package de.hsmainz.cs.semgis.arqextension.datatypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.hsmainz.cs.semgis.arqextension.Constants;
import de.hsmainz.cs.semgis.arqextension.vocabulary.WKT;

/** @author rbattle */
public class WKTLiteral extends GeoSPARQLLiteral {
	protected static final Logger LOG = LoggerFactory.getLogger(WKTLiteral.class);

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
		// TODO Auto-generated method stub
		return super.isEqual(value1, value2);
	}

	private static final Pattern pattern = Pattern.compile(
		"(<([^<>\"\\{\\}|^`\\\\]*)>)?[\\r\\n\\s]*([\\w\\(\\)\\d\\s\\r\\n.\\-,]*)");

	public WKTLiteral() {
		super(WKT.WKTLiteral.getURI());
	}

	/** {@inheritDoc} */
	@Override
	public Geometry doParse(String lexForm) throws DatatypeFormatException {
		String lexicalForm = lexForm.trim();
		Matcher m = pattern.matcher(lexicalForm);
		if (!m.matches()) {
			throw new DatatypeFormatException(lexicalForm, this,
				"Invalid or missing IRI/WKT");
		}
		if (3 != m.groupCount()) {
			throw new DatatypeFormatException(lexicalForm, this,
				"Invalid or missing IRI/WKT");
		}

		String iri = m.group(2);

		Integer srid = null;
		String auth = null;
		CoordinateReferenceSystem crs = DEFAULT_CRS;

		if (null != iri) {
			auth = getCoordinateReferenceSystemCode(iri);
			if (null == auth) {
				throw new DatatypeFormatException(
					lexicalForm,
					this,
					"Could not find coordinate reference system code.  Only CRS and EPSG are supported");
			}
			try {
				crs = CRS.decode(auth, false);
				srid = CRS.lookupEpsgCode(crs, false);
			} catch (NoSuchAuthorityCodeException e) {
				throw new DatatypeFormatException(lexicalForm, this,
					"Could not create geographic coordinate reference system");
			} catch (FactoryException e) {
				throw new DatatypeFormatException(lexicalForm, this,
					"Could not create geographic coordinate reference system");
			}
			if (null == crs) {
				throw new DatatypeFormatException(lexicalForm, this,
					"Unsupported IRI.  Could not find coordinate reference system");
			}

			if (null == srid) {
				srid = 0;
			}
		} else {
			srid =Constants.DEFAULT_SRID;
			auth = Constants.DEFAULT_CRS;
		}

		String wkt = m.group(3).toUpperCase();

		CoordinateSequenceFactory csf = CoordinateArraySequenceFactory.instance();
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(
			PrecisionModel.FLOATING), srid, csf);

		try {
			WKTReader reader = new WKTReader(factory);
			Geometry g = reader.read(wkt);
			g.setUserData(auth);
			return g;
		} catch (ParseException e) {
			throw new DatatypeFormatException(lexicalForm, this, "Invalid WKT");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String doUnparse(Geometry geometry) {
		String code = (String) geometry.getUserData();
		String uri = getCoordinateReferenceSystemURI(code);
		String text = geometry.toText();
		if (null == uri) {
			return text;
		} else {
			return "<" + uri + "> " + text;
		}
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public static class WKTConstructor extends ConstructorFunction<WKTLiteral> {
		public WKTConstructor() {
			super(new WKTLiteral());
		}
	}

	protected WKTLiteral(String uri) {
		super(uri);
	}

	public static class WKTLiteralSFNamespace extends WKTLiteral {
		public WKTLiteralSFNamespace() {
			super(WKT.DATATYPE_URI + "wktLiteral");
		}

		@Override
		public Geometry doParse(String value) {
			LOG.warn("Accepting WKT geometry with incorrect datatype sf:wktLiteral");
			return super.doParse(value);
		}
	}

	// @Override
	// public Object cannonicalise(Object value) {
	// return super.cannonicalise(value);
	// }
}
