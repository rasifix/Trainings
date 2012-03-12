package com.github.rasifix.trainings.geodeployer.internal;

import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import com.github.rasifix.trainings.PlaceNameLookup;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class ShapeFilePlaceNameLookup implements PlaceNameLookup {

	private SimpleFeatureSource features;

	public ShapeFilePlaceNameLookup(SimpleFeatureSource features) {
		this.features = features;
	}
	
	@Override
	public boolean containsPosition(double latitude, double longitude) {
		try {
			return features.getBounds().contains(new Coordinate(latitude, longitude));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String locationForPosition(double latitude, double longitude) {
		Point point = new GeometryFactory().createPoint(new Coordinate(latitude, longitude));
		
		try {
			SimpleFeature feature = findByIntersection(features, point);
			if (feature != null) {
				String attribute = (String) feature.getAttribute("NAME");
				return attribute;
			}
			
		} catch (CQLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

	private static SimpleFeature findByIntersection(SimpleFeatureSource featureSource, Point start) throws CQLException, IOException {
		Filter filter = CQL.toFilter("INTERSECTS(the_geom, " + start.toText() + ")");
		SimpleFeatureCollection filtered = featureSource.getFeatures(filter);
		if (filtered.size() == 1) {
			SimpleFeatureIterator it = filtered.features();
			SimpleFeature next = it.next();
			it.close();
			return next;
		}
		return null;
	}

}
