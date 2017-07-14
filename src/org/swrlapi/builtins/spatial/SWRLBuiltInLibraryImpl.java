package org.swrlapi.builtins.spatial;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgument;
import org.swrlapi.exceptions.InvalidSWRLBuiltInArgumentException;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.exceptions.SWRLBuiltInLibraryException;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import de.hsmainz.cs.semgis.arqextension.Constants;
import de.hsmainz.cs.semgis.arqextension.SpatialFunctionBase;
import de.hsmainz.cs.semgis.arqextension.datatypes.GMLLiteral;
import de.hsmainz.cs.semgis.arqextension.datatypes.WKTLiteral;

public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary {

	 private static final String PREFIX = "temporal";

	  private static final String NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#";

	  private static final String[] BUILT_IN_NAMES = { "xmax", "ymax", "ymin", "xmin", "affine",
	    "area", "centroid", "closestPoint", "concaveHull", "delaunayTriangles", "dimension", "disstanceSphere",
	    "dump", "dumpPoints", "flipCoordinates", "geometryN", "geometryType", "hausdorffDistance", "isEmpty",
	    "isSimple", "isValid", "isValidDetail", "isValidReason", "length", "minimumBoundingCircle", "numGeometries",
	    "numPoints", "perimeter", "pointN", "polygonize", "reverse", "rotate",
	    "scale", "shortestLine", "simplify", "simplifyPreserveTopology", "snap", "srid", "startPoint",
	    "transform","translate","unaryunion","X","Y","azimuth" };
	
	
	  private static final String POINT="point";
	  
	  private static final String POLYGON="polygon";
	  
	  private static final String LINESTRING="linestring";
	  
	  private static final String RASTER="raster";

	  
 

	  public SWRLBuiltInLibraryImpl()
	  {
	    super(PREFIX, NAMESPACE, new HashSet<>(Arrays.asList(BUILT_IN_NAMES)));
	  }

	  
	  
	  public static Geometry parseGeometryFromString(String geometryString,String geometryType){
		  Geometry geometry;
		  try{
		  WKTLiteral literal=new WKTLiteral();
		  	geometry=literal.doParse(geometryString);
		  	if(geometryType!=null && !geometry.getGeometryType().equals(geometryType.toUpperCase())){
			      throw new InvalidSWRLBuiltInArgumentException(0,
			    	        "expecting "+geometryType+" argument for comparison, got " + geometry.getGeometryType());
		  	}
		  	return geometry;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GMLLiteral literal=new GMLLiteral();
			  geometry=literal.doParse(geometryString);
			  	if(geometryType!=null && !geometry.getGeometryType().equals(geometryType.toUpperCase())){
				      throw new InvalidSWRLBuiltInArgumentException(0,
				    	        "expecting "+geometryType+" argument for comparison, got " + geometry.getGeometryType());
			  	}
			  return geometry;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return null;
	  }
	  
	  public boolean azimuth(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Point g1 = getArgumentAsAPoint(0, arguments);
		  Point g2 = getArgumentAsAPoint(1, arguments);
		   GeodeticCalculator calc=new GeodeticCalculator();
			if(g1 instanceof Point && g2 instanceof Point){
				Point2D point1=new java.awt.Point();
				point1.setLocation(g1.getCoordinate().x, g1.getCoordinate().y);
				Point2D point2=new java.awt.Point();
				point2.setLocation(g2.getCoordinate().x, g2.getCoordinate().y);
				calc.setStartingGeographicPoint(point1);
				calc.setDestinationGeographicPoint(point2);
				return processResultArgument(arguments, 0, calc.getAzimuth());
			}
			throw new SWRLBuiltInException("Azimuth could not be calculated!");
	  }
	  
	  public boolean X(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Point point = getArgumentAsAPoint(0, arguments);
		  return processResultArgument(arguments, 0, point.getX());
	  }
	  
	  public boolean Y(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Point point = getArgumentAsAPoint(0, arguments);
		  return processResultArgument(arguments, 0, point.getX());
	  }
	  	  
	  public boolean makePoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Double arg0 = getArgumentAsADouble(0, arguments);
		  Double arg1 = getArgumentAsADouble(1, arguments);
		  GeometryBuilder builder = new GeometryBuilder();
		  return processResultArgument(arguments, 0, builder.point(arg0, arg1));
	  }
	  
	  public boolean makePointM(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(3, arguments.size());
		  Double arg0 = getArgumentAsADouble(0, arguments);
		  Double arg1 = getArgumentAsADouble(1, arguments);
		  Double arg2 = getArgumentAsADouble(1, arguments);
		  GeometryBuilder builder = new GeometryBuilder();
		  return processResultArgument(arguments, 0, builder.pointZ(arg0, arg1,arg2));
	  }
	  
	  public boolean endPoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  LineString arg0 = getArgumentAsALineString(0, arguments);
		  return processResultArgument(arguments, 0, arg0.getEndPoint());
	  }
	  
	  public boolean isClosed(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  LineString arg0 = getArgumentAsALineString(0, arguments);
		  return processResultArgument(arguments, 0, arg0.isClosed());
	  }
	  	  
	  public boolean isRing(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  LineString arg0 = getArgumentAsALineString(0, arguments);
		  return processResultArgument(arguments, 0, arg0.isRing());
	  }
	  
	  public boolean makeLine(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  LineMerger linemerge=new LineMerger();
		  Integer i=0;
		  for(SWRLBuiltInArgument argument:arguments){
			  linemerge.add(getArgumentAsALineString(i, arguments));
			  i++;
		  }
		  return processResultArgument(arguments, 0, (LineString)linemerge.getMergedLineStrings().iterator().next());
	  }
	  
	  public boolean area(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry geometry = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, geometry.getArea());
	  }
	  
	  public boolean centroid(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry geometry = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, geometry.getCentroid());
	  }
	  
	  public boolean closestPoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g1 = getArgumentAsAGeometry(0, arguments);
		  Geometry g2 = getArgumentAsAGeometry(1, arguments);
		  GeometryFactory fact=new GeometryFactory();
		  DistanceOp distop=new DistanceOp(g1,g2);
		  Coordinate[] coords=distop.nearestPoints();
		  Coordinate coord=new Coordinate(coords[0].x,coords[0].y);
		  Point point=fact.createPoint(coord);		
		  return processResultArgument(arguments, 0, point);
	  }
	  
	  public boolean concaveHull(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Double d = getArgumentAsADouble(0, arguments);
		  org.opensphere.geometry.algorithm.ConcaveHull hull=new org.opensphere.geometry.algorithm.ConcaveHull(g,d);
		  return processResultArgument(arguments, 0, hull.getConcaveHull());
	  }
	  
	  public boolean dimension(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.getDimension());
	  }
	  
	  public boolean distanceSphere(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g1 = getArgumentAsAGeometry(0, arguments);
		  Geometry g2 = getArgumentAsAGeometry(0, arguments);
			if(g1 instanceof Point && g2 instanceof Point){
				return processResultArgument(arguments, 0, calculateDistance(null, new Point[]{(Point)g1,(Point)g2}));
			}else{
				return processResultArgument(arguments, 0, calculateDistance(null, new Point[]{g1.getInteriorPoint(),g2.getInteriorPoint()}));
			}
	  }
	  
	  public boolean isEmpty(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.isEmpty());
	  }
	  
	  public boolean isSimple(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.isSimple());
	  }
	  
	  public boolean isValid(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.isValid());
	  }
	  
	  public boolean length(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.getLength());
	  }
	  
	  public boolean minimumBoundingCircle(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		com.vividsolutions.jts.algorithm.MinimumBoundingCircle mincircle=new com.vividsolutions.jts.algorithm.MinimumBoundingCircle(g);
		  return processResultArgument(arguments, 0, mincircle.getCircle());
	  }
	  
	  public boolean numGeometries(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.getNumGeometries());
	  }
	  
	  public boolean numPoints(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.getNumGeometries());
	  }
	  
	  public boolean perimeter(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.getLength());
	  }
	  
	  
	  public boolean pointN(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  GeometryBuilder builder=new GeometryBuilder();
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Integer n = getArgumentAsAnInt(1, arguments);
		  return processResultArgument(arguments, 0, builder.point(g.getCoordinates()[n.intValue()].x, g.getCoordinates()[n.intValue()].y));
	  }
	  
	  public boolean reverse(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g.reverse());
	  }
	  
	  public boolean rotate(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Double n = getArgumentAsADouble(1, arguments);
			AffineTransformation trans=new AffineTransformation();
			trans=trans.rotate(n);
		  return processResultArgument(arguments, 0, trans.transform(g));
	  }
	  
	  public boolean scale(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(3, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Double n = getArgumentAsADouble(1, arguments);
		  Double n2 = getArgumentAsADouble(2, arguments);
		  AffineTransformation trans=new AffineTransformation();
		  trans.scale(n, n2);
		  return processResultArgument(arguments, 0, trans.transform(g));
	  }
	  
	  public boolean shortestLine(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  Geometry g1 = getArgumentAsAGeometry(0, arguments);
		  Geometry g2 = getArgumentAsAGeometry(1, arguments);
		  GeometryFactory factory=new GeometryFactory();
	      DistanceOp distop=new DistanceOp(g1,g2);
		  Coordinate[] coord=distop.nearestPoints();
		  return processResultArgument(arguments, 0, factory.createLineString(coord));
	  }
	  
	  public boolean simplify(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Double n = getArgumentAsADouble(1, arguments);
		  Boolean b = getArgumentAsABoolean(2, arguments);
		  DouglasPeuckerSimplifier simpl=new DouglasPeuckerSimplifier(g);
		  simpl.setDistanceTolerance(n);
		  simpl.setEnsureValid(b);
		  return processResultArgument(arguments, 0, simpl.getResultGeometry());
	  }
	  
	  public boolean simplifyPreserveTopology(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  Double n = getArgumentAsADouble(1, arguments);
		  Boolean b = getArgumentAsABoolean(2, arguments);
		  TopologyPreservingSimplifier topo=new TopologyPreservingSimplifier(g);	
		  return processResultArgument(arguments, 0, topo.getResultGeometry());
	  }

	  
	  public boolean snap(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(3, arguments.size());
		  Geometry g1 = getArgumentAsAGeometry(0, arguments);
		  Geometry g2 = getArgumentAsAGeometry(1, arguments);
		  Double n = getArgumentAsADouble(2, arguments);
		  GeometrySnapper snapper=new GeometrySnapper(g1);
		  return processResultArgument(arguments, 0, snapper.snapTo(g2, n));
	  }
	  
	  public boolean srid(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g1 = getArgumentAsAGeometry(0, arguments);
		  return processResultArgument(arguments, 0, g1.getSRID());
	  }
	  
	  public boolean startPoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  GeometryBuilder builder=new GeometryBuilder();
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  if(!g.isEmpty())
			  return processResultArgument(arguments, 0, builder.point(g.getCoordinates()[0].x, g.getCoordinates()[0].y));
			throw new SWRLBuiltInException("Geometry is empty!");
	  }
	  
	  public boolean transform(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException, NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  GeometryBuilder builder=new GeometryBuilder();
		  Geometry g = getArgumentAsAGeometry(0, arguments);
			Integer srid= getArgumentAsAnInt(1, arguments);
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:"+g.getSRID());
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:"+srid);
			MathTransform transform=CRS.findMathTransform(sourceCRS, targetCRS);
			Geometry targetGeometry = JTS.transform( g, transform);
			return processResultArgument(arguments, 0, targetGeometry);
	  }
	  
	  public boolean translate(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(3, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
			Double deltax= getArgumentAsADouble(1, arguments);
			Double deltay= getArgumentAsADouble(2, arguments);
			AffineTransformation transform=new AffineTransformation();
			transform.translate(deltax, deltay);
		  return processResultArgument(arguments, 0, transform.transform(g));
	  }
	  
	  public boolean transScale(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(5, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
			Double deltax= getArgumentAsADouble(1, arguments);
			Double deltay= getArgumentAsADouble(2, arguments);
			Double xfactor= getArgumentAsADouble(3, arguments);
			Double yfactor= getArgumentAsADouble(4, arguments);
			AffineTransformation transform=new AffineTransformation();
			transform.translate(deltax, deltay);
			transform.scale(xfactor, yfactor);
		  return processResultArgument(arguments, 0, transform.transform(g));
	  }
	  
	  public boolean unaryUnion(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  Geometry g = getArgumentAsAGeometry(0, arguments);
		  UnaryUnionOp op=new UnaryUnionOp(g);
		  return processResultArgument(arguments, 0, op.union());
	  }
	  
	  public boolean covers(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.covers(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).covers(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  
	  public boolean coveredBy(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
		  return processResultArgument(arguments, 0, JTS.toGeometry(raster.getEnvelope2D().getBounds2D()));
	  }
	  
	  public boolean disjoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.disjoint(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).disjoint(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean crosses(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.crosses(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).crosses(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean contains(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.contains(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).contains(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  
	  public boolean intersects(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.intersects(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).intersects(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean intersection(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.intersection(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).intersection(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean equals(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.equalsExact(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).equalsExact(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean touches(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.touches(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).touches(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }
	  
	  public boolean within(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(2, arguments.size());
		  try{
			  Geometry g1 = getArgumentAsAGeometry(0, arguments);
			  Geometry g2 = getArgumentAsAGeometry(1, arguments);
			  return processResultArgument(arguments, 0, g1.within(g2));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  try{
			  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
			  GridCoverage2D raster2 = getArgumentAsACoverage(1, arguments);
			  Rectangle2D bbox1=raster.getEnvelope2D().getBounds2D();
			  Rectangle2D bbox2=raster2.getEnvelope2D().getBounds2D();
			  return processResultArgument(arguments, 0, JTS.toGeometry(bbox1.getBounds()).within(JTS.toGeometry(bbox2.getBounds())));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			throw new SWRLBuiltInException("No valid geometry!");
	  }

	  
	  public boolean width(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
		  return processResultArgument(arguments, 0, raster.getRenderedImage().getWidth());
	  }
	  
	  public boolean height(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(1, arguments.size());
		  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
		  return processResultArgument(arguments, 0, raster.getRenderedImage().getHeight());
	  }
	  
	  
	  public boolean pixelAsPoint(@NonNull List<@NonNull SWRLBuiltInArgument> arguments) throws SWRLBuiltInException{
		  checkNumberOfArgumentsEqualTo(3, arguments.size());
		  GridCoverage2D raster = getArgumentAsACoverage(0, arguments);
		  Integer x = getArgumentAsAnInt(1, arguments);
		  Integer y = getArgumentAsAnInt(2, arguments);
			GeometryBuilder builder=new GeometryBuilder();
			Envelope2D pixelEnvelop;
			try {
				pixelEnvelop = raster.getGridGeometry().gridToWorld(new GridEnvelope2D(x, y, 1, 1));
				return  processResultArgument(arguments, 0, builder.point(pixelEnvelop.getCenterX(), pixelEnvelop.getCenterY()));
			} catch (TransformException e) {
				throw new SWRLBuiltInException("No valid geometry!");
			}

	  }
	  
	  public static Double calculateDistance(CoordinateReferenceSystem crs, Point[] points){
		    if (crs == null) {
	            crs = DefaultGeographicCRS.WGS84;
	    }
	    double distance = 0.0;
	    try {
	            distance = JTS.orthodromicDistance(
	                            points[0].getCoordinate(),
	                            points[1].getCoordinate(), crs);
	    } catch (TransformException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    }
	    return distance;
	  }
	  

	  
	 
	@Override
	public void reset() throws SWRLBuiltInLibraryException {
		// TODO Auto-generated method stub
		
	}

}
