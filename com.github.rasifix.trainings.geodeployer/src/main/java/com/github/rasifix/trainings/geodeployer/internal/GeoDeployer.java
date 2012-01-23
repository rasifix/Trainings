package com.github.rasifix.trainings.geodeployer.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

import com.github.rasifix.trainings.ElevationModel;
import com.github.rasifix.trainings.application.Deployer;

@Component
public class GeoDeployer implements Deployer {

	private ComponentContext context;

	@Activate
	public void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Deactivate
	public void deactivate() {
		this.context = null;
	}
	
	@Override
	public boolean canDeploy(File file) {
		return file.getName().toLowerCase().endsWith(".agr");
	}

	@Override
	public void deploy(File file) throws IOException {
		System.out.println("deploying ElevationModel from " + file.getName());
		// TODO: make transformation generally applicable
		ArcGridElevationModel elevationModel = new ArcGridElevationModel(new FileInputStream(file));
		TransformingElevationModel transformingModel = new TransformingElevationModel(elevationModel);
		context.getBundleContext().registerService(ElevationModel.class.getName(), transformingModel, null);
	}

}
