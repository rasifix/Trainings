package com.github.rasifix.trainings.geodeployer.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.github.rasifix.osgi.application.Deployer;
import com.github.rasifix.trainings.ElevationModel;

@Component
public class GeoDeployer implements Deployer {

	private ComponentContext context;
	
	@Activate
	public void doActivate(ComponentContext context) {
		System.out.println("activating GeoDeployer");
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
		try {			
			System.out.println("deploying ElevationModel from " + file.getName());
			// TODO: make transformation generally applicable
			ArcGridElevationModel elevationModel = new ArcGridElevationModel(new FileInputStream(file));
			TransformingElevationModel transformingModel = new TransformingElevationModel(elevationModel);
			context.getBundleContext().registerService(ElevationModel.class.getName(), transformingModel, null);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
