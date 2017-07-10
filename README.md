# SemGIS_ARQExtension
An extension of the ARQ Query Processor in order to support more POSTGIS functions

The support for geometric operations in SPARQL is at the current time limited to the GeoSPARQL specification http://www.opengeospatial.org/standards/geosparql

While there are other unofficial triple-store specific standards implementing similar functionality e.g. Strabon (http://www.strabon.di.uoa.gr), none of the solutions so far represents the full variety of functions available in a traditional GIS database like POSTGIS.

This project intends to extend the ARQ query processor of Apache Jena with functions present in POSTGIS and other relational spatial databases and with support for Raster data operations using Java libraries (e.g. Geotools, JTS)

An extended GeoSPARQL vocabulary not only enables more querying opportunities but also paves the way for an extension of geospatial reasoning using the SPIN reasoning engine, as it relies on SPARQL functions to create reasoning rules.
