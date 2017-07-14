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
package de.hsmainz.cs.semgis.arqextension;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class TransformCache {

   private static final int CACHE_SIZE = 10;

   private LinkedHashMap<CoordinateReferenceSystem, LinkedHashMap<CoordinateReferenceSystem, MathTransform>> cache;

   public TransformCache() {
      this.cache = new LinkedHashMap<CoordinateReferenceSystem, LinkedHashMap<CoordinateReferenceSystem,MathTransform>>(CACHE_SIZE) {
         private static final long serialVersionUID = -7455036937026506842L;

         @Override
         protected boolean removeEldestEntry(
               Entry<CoordinateReferenceSystem, LinkedHashMap<CoordinateReferenceSystem, MathTransform>> eldest) {
            return (size() > CACHE_SIZE);

         }
      };
   }

   public MathTransform getTransform(CoordinateReferenceSystem source, CoordinateReferenceSystem target) throws FactoryException {
      LinkedHashMap<CoordinateReferenceSystem, MathTransform> transforms = cache.get(source);
      if (null == transforms) {
         transforms = new LinkedHashMap<CoordinateReferenceSystem, MathTransform>(CACHE_SIZE) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(
                  Entry<CoordinateReferenceSystem, MathTransform> eldest) {
               return (size() > CACHE_SIZE);

            }
         };
         cache.put(source, transforms);
      }
      MathTransform transform = transforms.get(target);
      if (null == transform) {
         transform = CRS.findMathTransform(source, target);
         transforms.put(target, transform);
      }
      return transform;
   }

}

