package de.hsmainz.cs.semgis.arqextension;

import com.bbn.parliament.jena.graph.index.spatial.Constants;
import com.bbn.parliament.jena.graph.index.spatial.IterableFunctionFactory;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.datatypes.GMLLiteral;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.datatypes.WKTLiteral;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Contains;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_CoveredBy;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Covers;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Disjoint;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Equals;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Inside;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Meet;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.EH_Overlap;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_DC;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_EC;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_EQ;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_NTTP;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_NTTPI;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_PO;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_TPP;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.RCC8_TPPI;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Contains;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Crosses;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Disjoint;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Equals;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Intersects;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Overlaps;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Touches;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.RelationFunction.SF_Within;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.util.CreateWKTPoint;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.util.LiteralToGeometryType;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.function.util.Transform;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.vocabulary.GML;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.vocabulary.Geof;
import com.bbn.parliament.jena.graph.index.spatial.geosparql.vocabulary.WKT;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.sparql.function.Function;
import de.hsmainz.cs.semgis.querytranslator.arqextension.OffsetCurve;
import de.hsmainz.cs.semgis.querytranslator.arqextension.SetPoint;
import de.hsmainz.cs.semgis.querytranslator.arqextension.Split;
import de.hsmainz.cs.semgis.querytranslator.arqextension.SwapOrdinates;
import de.hsmainz.cs.semgis.querytranslator.arqextension.envelope.XMax;
import de.hsmainz.cs.semgis.querytranslator.arqextension.envelope.XMin;
import de.hsmainz.cs.semgis.querytranslator.arqextension.envelope.YMax;
import de.hsmainz.cs.semgis.querytranslator.arqextension.envelope.YMin;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Area;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Centroid;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.ClosestPoint;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.ConcaveHull;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.DelaunayTriangles;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Dimension;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.FlipCoordinates;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.GeometryN;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.GeometryType;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.HausdorffDistance;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.IsEmpty;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.IsSimple;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.IsValid;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.IsValidReason;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Length;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.NumGeometries;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.NumPoints;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Perimeter;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.PointN;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Reverse;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Rotate;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.SRID;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Scale;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Simplify;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.SimplifyPreserveTopology;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Snap;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.StartPoint;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.TransScale;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.Translate;
import de.hsmainz.cs.semgis.querytranslator.arqextension.geometry.UnaryUnion;
import de.hsmainz.cs.semgis.querytranslator.arqextension.linestring.IsClosed;
import de.hsmainz.cs.semgis.querytranslator.arqextension.linestring.IsRing;
import de.hsmainz.cs.semgis.querytranslator.arqextension.linestring.MakeLine;
import de.hsmainz.cs.semgis.querytranslator.arqextension.linestring.Segmentize;
import de.hsmainz.cs.semgis.querytranslator.arqextension.point.Azimuth;
import de.hsmainz.cs.semgis.querytranslator.arqextension.point.X;
import de.hsmainz.cs.semgis.querytranslator.arqextension.point.Y;
import de.hsmainz.cs.semgis.querytranslator.arqextension.polygon.MakePolygon;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Band;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.BandMetaData;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.BandNoDataValue;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.BandPixelType;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Clip;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.HasNoBand;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Height;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.NearestValue;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.NumBands;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.PixelAsPoint;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.PixelHeight;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.RasterToWorldCoord;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.RasterToWorldCoordX;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.RasterToWorldCoordY;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Summary;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.UpperLeftX;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.UpperLeftY;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Width;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.WorldToRasterCoord;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.WorldToRasterCoordX;
import de.hsmainz.cs.semgis.querytranslator.arqextension.raster.WorldToRasterCoordY;
import de.hsmainz.cs.semgis.querytranslator.arqextension.vocabulary.PostGISGeo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GeoSPARQLFunctionFactory implements IterableFunctionFactory {

    public Map<String, Class<? extends SpatialFunctionBase>> functions;

    public GeoSPARQLFunctionFactory() {
        functions = new HashMap<>();
        functions.put(Geof.distance.getURI(), Distance.class);
        functions.put(Geof.buffer.getURI(), Buffer.class);
        functions.put(Geof.convexHull.getURI(), ConvexHull.class);
        functions.put(Geof.intersection.getURI(), Intersection.class);
        functions.put(Geof.union.getURI(), Union.class);
        functions.put(Geof.difference.getURI(), Difference.class);
        functions.put(Geof.symDifference.getURI(), SymDifference.class);
        functions.put(Geof.envelope.getURI(), Envelope.class);
        functions.put(Geof.boundary.getURI(), Boundary.class);
        functions.put(Geof.relate.getURI(), Relate.class);

        // OGC Simple Features functions
        functions.put(Geof.sf_equals.getURI(), SF_Equals.class);
        functions.put(Geof.sf_disjoint.getURI(), SF_Disjoint.class);
        functions.put(Geof.sf_intersects.getURI(), SF_Intersects.class);
        functions.put(Geof.sf_touches.getURI(), SF_Touches.class);
        functions.put(Geof.sf_crosses.getURI(), SF_Crosses.class);
        functions.put(Geof.sf_within.getURI(), SF_Within.class);
        functions.put(Geof.sf_contains.getURI(), SF_Contains.class);
        functions.put(Geof.sf_overlaps.getURI(), SF_Overlaps.class);

        // Egenhofer functions
        functions.put(Geof.eh_equals.getURI(), EH_Equals.class);
        functions.put(Geof.eh_disjoint.getURI(), EH_Disjoint.class);
        functions.put(Geof.eh_meet.getURI(), EH_Meet.class);
        functions.put(Geof.eh_overlap.getURI(), EH_Overlap.class);
        functions.put(Geof.eh_covers.getURI(), EH_Covers.class);
        functions.put(Geof.eh_coveredBy.getURI(), EH_CoveredBy.class);
        functions.put(Geof.eh_inside.getURI(), EH_Inside.class);
        functions.put(Geof.eh_contains.getURI(), EH_Contains.class);

        // RCC8 functions
        functions.put(Geof.rcc8_eq.getURI(), RCC8_EQ.class);
        functions.put(Geof.rcc8_dc.getURI(), RCC8_DC.class);
        functions.put(Geof.rcc8_ec.getURI(), RCC8_EC.class);
        functions.put(Geof.rcc8_po.getURI(), RCC8_PO.class);
        functions.put(Geof.rcc8_tppi.getURI(), RCC8_TPPI.class);
        functions.put(Geof.rcc8_tpp.getURI(), RCC8_TPP.class);
        functions.put(Geof.rcc8_ntpp.getURI(), RCC8_NTTP.class);
        functions.put(Geof.rcc8_ntppi.getURI(), RCC8_NTTPI.class);

        //POSTGIS functions
        functions.put(PostGISGeo.st_area.getURI(), Area.class);
        functions.put(PostGISGeo.st_azimuth.getURI(), Azimuth.class);
        functions.put(PostGISGeo.st_band.getURI(), Band.class);
        functions.put(PostGISGeo.st_bandmetadata.getURI(), BandMetaData.class);
        functions.put(PostGISGeo.st_bandnodatavalue.getURI(), BandNoDataValue.class);
        functions.put(PostGISGeo.st_bandpixeltype.getURI(), BandPixelType.class);
        functions.put(PostGISGeo.st_centroid.getURI(), Centroid.class);
        functions.put(PostGISGeo.st_clip.getURI(), Clip.class);
        functions.put(PostGISGeo.st_closestPoint.getURI(), ClosestPoint.class);
        functions.put(PostGISGeo.st_concaveHull.getURI(), ConcaveHull.class);
        functions.put(PostGISGeo.st_delaunayTriangles.getURI(), DelaunayTriangles.class);
        functions.put(PostGISGeo.st_dimension.getURI(), Dimension.class);
        functions.put(PostGISGeo.st_endPoint.getURI(), Dimension.class);
        functions.put(PostGISGeo.st_flipCoordinates.getURI(), FlipCoordinates.class);
        functions.put(PostGISGeo.st_geometryN.getURI(), GeometryN.class);
        functions.put(PostGISGeo.st_geometryType.getURI(), GeometryType.class);
        functions.put(PostGISGeo.st_hasNoBand.getURI(), HasNoBand.class);
        functions.put(PostGISGeo.st_height.getURI(), Height.class);
        functions.put(PostGISGeo.st_hausdorffDistance.getURI(), HausdorffDistance.class);
        functions.put(PostGISGeo.st_isClosed.getURI(), IsClosed.class);
        functions.put(PostGISGeo.st_isEmpty.getURI(), IsEmpty.class);
        functions.put(PostGISGeo.st_isRing.getURI(), IsRing.class);
        functions.put(PostGISGeo.st_isSimple.getURI(), IsSimple.class);
        functions.put(PostGISGeo.st_isValid.getURI(), IsValid.class);
        functions.put(PostGISGeo.st_isValidReason.getURI(), IsValidReason.class);
        functions.put(PostGISGeo.st_Length.getURI(), Length.class);
        functions.put(PostGISGeo.st_Length2D.getURI(), Length.class);
        functions.put(PostGISGeo.st_makeLine.getURI(), MakeLine.class);
        //functions.put(PostGISGeo.st_makePointM.getURI(), MakePointM.class);
        functions.put(PostGISGeo.st_makePolygon.getURI(), MakePolygon.class);
        functions.put(PostGISGeo.st_nearestValue.getURI(), NearestValue.class);
        functions.put(PostGISGeo.st_numBands.getURI(), NumBands.class);
        functions.put(PostGISGeo.st_numGeometries.getURI(), NumGeometries.class);
        functions.put(PostGISGeo.st_numPoints.getURI(), NumPoints.class);
        functions.put(PostGISGeo.st_nPoints.getURI(), NumPoints.class);
        functions.put(PostGISGeo.st_offsetCurve.getURI(), OffsetCurve.class);
        functions.put(PostGISGeo.st_perimeter.getURI(), Perimeter.class);
        functions.put(PostGISGeo.st_perimeter2D.getURI(), Perimeter.class);
        functions.put(PostGISGeo.st_pixelAsPoint.getURI(), PixelAsPoint.class);
        functions.put(PostGISGeo.st_pixelHeight.getURI(), PixelHeight.class);
        functions.put(PostGISGeo.st_pointN.getURI(), PointN.class);
        functions.put(PostGISGeo.st_rast_isEmpty.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.IsEmpty.class);
        functions.put(PostGISGeo.st_rast_Contains.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Contains.class);
        functions.put(PostGISGeo.st_rast_Covers.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Covers.class);
        functions.put(PostGISGeo.st_rast_CoveredBy.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.CoveredBy.class);
        functions.put(PostGISGeo.st_rast_Disjoint.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Disjoint.class);
        functions.put(PostGISGeo.st_rast_Intersects.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Intersects.class);
        functions.put(PostGISGeo.st_rast_Overlaps.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Overlaps.class);
        functions.put(PostGISGeo.st_rast_srid.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.SRID.class);
        functions.put(PostGISGeo.st_rast_Touches.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Touches.class);
        functions.put(PostGISGeo.st_rast_Within.getURI(), de.hsmainz.cs.semgis.querytranslator.arqextension.raster.Within.class);
        functions.put(PostGISGeo.st_rasterToWorldCoord.getURI(), RasterToWorldCoord.class);
        functions.put(PostGISGeo.st_rasterToWorldCoordX.getURI(), RasterToWorldCoordX.class);
        functions.put(PostGISGeo.st_rasterToWorldCoordY.getURI(), RasterToWorldCoordY.class);
        functions.put(PostGISGeo.st_reverse.getURI(), Reverse.class);
        functions.put(PostGISGeo.st_rotate.getURI(), Rotate.class);
        functions.put(PostGISGeo.st_scale.getURI(), Scale.class);
        functions.put(PostGISGeo.st_segmentize.getURI(), Segmentize.class);
        functions.put(PostGISGeo.st_setPoint.getURI(), SetPoint.class);
        functions.put(PostGISGeo.st_simplify.getURI(), Simplify.class);
        functions.put(PostGISGeo.st_simplifyPreserveTopology.getURI(), SimplifyPreserveTopology.class);
        functions.put(PostGISGeo.st_snap.getURI(), Snap.class);
        functions.put(PostGISGeo.st_split.getURI(), Split.class);
        functions.put(PostGISGeo.st_srid.getURI(), SRID.class);
        functions.put(PostGISGeo.st_startPoint.getURI(), StartPoint.class);
        functions.put(PostGISGeo.st_summary.getURI(), Summary.class);
        functions.put(PostGISGeo.st_swapOrdinates.getURI(), SwapOrdinates.class);
        functions.put(PostGISGeo.st_transscale.getURI(), TransScale.class);
        functions.put(PostGISGeo.st_translate.getURI(), Translate.class);
        functions.put(PostGISGeo.st_transform.getURI(), Transform.class);
        functions.put(PostGISGeo.st_upperLeftX.getURI(), UpperLeftX.class);
        functions.put(PostGISGeo.st_upperLeftY.getURI(), UpperLeftY.class);
        functions.put(PostGISGeo.st_unaryUnion.getURI(), UnaryUnion.class);
        functions.put(PostGISGeo.st_width.getURI(), Width.class);
        functions.put(PostGISGeo.st_worldToRasterCoord.getURI(), WorldToRasterCoord.class);
        functions.put(PostGISGeo.st_worldToRasterCoordX.getURI(), WorldToRasterCoordX.class);
        functions.put(PostGISGeo.st_worldToRasterCoordY.getURI(), WorldToRasterCoordY.class);
        functions.put(PostGISGeo.st_unaryUnion.getURI(), UnaryUnion.class);
        functions.put(PostGISGeo.st_x.getURI(), X.class);
        functions.put(PostGISGeo.st_xMin.getURI(), XMin.class);
        functions.put(PostGISGeo.st_xMax.getURI(), XMax.class);
        functions.put(PostGISGeo.st_y.getURI(), Y.class);
        functions.put(PostGISGeo.st_yMin.getURI(), YMin.class);
        functions.put(PostGISGeo.st_yMax.getURI(), YMax.class);

        // extra functions
        // constructors for literals
        functions.put(WKT.WKTLiteral.getURI(), WKTLiteral.WKTConstructor.class);
        functions.put(GML.GMLLiteral.getURI(), GMLLiteral.GMLConstructor.class);

        // extra utility functions
        functions.put(Constants.SPATIAL_FUNCTION_NS + "transform", Transform.class);
        functions.put(Constants.SPATIAL_FUNCTION_NS + "makeWKTPoint", CreateWKTPoint.class);
        functions.put(Constants.SPATIAL_FUNCTION_NS + "WKTToGeometryPoint", LiteralToGeometryType.class);
    }

    @Override
    public Function create(String uri) {
        Class<? extends SpatialFunctionBase> imp = functions.get(uri);
        if (null == imp) {
            throw new QueryExecException(String.format("%s is not a valid function", uri));
        }
        try {
            return imp.newInstance();
        } catch (InstantiationException e) {
            throw new QueryExecException(String.format("Could not instantiate function for %s", uri), e);
        } catch (IllegalAccessException e) {
            throw new QueryExecException(String.format("Could not instantiate function for %s", uri), e);
        }
    }

    public String[] getURIs() {
        return functions.keySet().toArray(new String[]{});
    }

    @Override
    public Iterator<String> iterator() {
        return functions.keySet().iterator();
    }

}
