package com.github.rasifix.trainings.format.gpx.internal;

import java.io.IOException;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.integration.resource.Resource;

@Component(properties={ "com.github.rasifix.trainings.format=gpx" })
public class GpxFormat implements Format {

	@Override
	public boolean canRead(Resource resource) throws IOException {
		final String filename = resource.getName().toLowerCase();
		return filename.toLowerCase().endsWith(".gpx");
	}

	@Override
	public ActivityReader createReader() {
		return new GpxReader();
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
