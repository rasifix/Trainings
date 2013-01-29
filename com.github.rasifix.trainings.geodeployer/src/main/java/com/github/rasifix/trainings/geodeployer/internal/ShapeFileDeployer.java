package com.github.rasifix.trainings.geodeployer.internal;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

import com.github.rasifix.osgi.application.Deployer;
import com.github.rasifix.trainings.PlaceNameLookup;

@Component
public class ShapeFileDeployer implements Deployer {

	private ComponentContext context;

	@Activate
	public void doActivate(ComponentContext context) {
		this.context = context;
	}
	
	@Deactivate
	public void doDeactivate() {
		this.context = null;
	}

	@Override
	public boolean canDeploy(File file) {
		return file.getName().endsWith(".shp");
	}

	@Override
	public void deploy(File file) throws IOException {
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource features = store.getFeatureSource();
		
		System.out.println("deploying PlaceNameLookup from " + file.getName());
		
		PlaceNameLookup lookup = new ShapeFilePlaceNameLookup(features);
		PlaceNameLookup transformingLookup = new TransformingPlaceNameLookup(lookup);
		
		context.getBundleContext().registerService(PlaceNameLookup.class.getName(), transformingLookup, null);

	}

}
