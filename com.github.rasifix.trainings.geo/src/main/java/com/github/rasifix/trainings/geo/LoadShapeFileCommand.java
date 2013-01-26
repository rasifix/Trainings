package com.github.rasifix.trainings.geo;

import java.io.File;
import java.io.IOException;

import jline.Completor;
import jline.FileNameCompletor;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Component
public class LoadShapeFileCommand implements Command {
	
	private static final String NAME = "shape";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public Completor getCompletor() {
		return new FileNameCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		File file = new File(context.getArgument(0));
		int x = Integer.parseInt(context.getArgument(1));
		int y = Integer.parseInt(context.getArgument(2));
		
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		
		Point point = new GeometryFactory().createPoint(new Coordinate(x, y));
		SimpleFeature feature = findByIntersection(featureSource, point);
		
		if (feature != null) {
			System.out.println("NAME = " + feature.getAttribute("NAME"));
		}
		
		return context.getCurrent();
	}

	private static SimpleFeature findByIntersection(SimpleFeatureSource featureSource, Point start) throws CQLException, IOException {
		Filter filter = CQL.toFilter("INTERSECTS(the_geom, " + start.toText() + ")");
		SimpleFeatureCollection filtered = featureSource.getFeatures(filter);
		if (filtered.size() == 1) {
			return filtered.features().next();
		}
		return null;
	}

}
