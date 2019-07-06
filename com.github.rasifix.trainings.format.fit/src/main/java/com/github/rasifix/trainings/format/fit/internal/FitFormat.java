package com.github.rasifix.trainings.format.fit.internal;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.integration.resource.Resource;

@Component(properties={ "com.github.rasifix.trainings.format=fit" })
public class FitFormat implements Format {
	
	@Override
	public boolean canRead(Resource resource) throws IOException {
		final String filename = resource.getName().toLowerCase();
		return filename.toLowerCase().endsWith(".fit");
	}
	
	@Override
	public ActivityReader createReader() {
		return new FitReader();
	}
	
	@Override
	public boolean canWrite() {
		return false;
	}
	
	@Override
	public ActivityWriter createWriter() {
		throw new UnsupportedOperationException();
	}
	
}
