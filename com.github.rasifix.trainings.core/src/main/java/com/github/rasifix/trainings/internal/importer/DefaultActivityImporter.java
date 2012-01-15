package com.github.rasifix.trainings.internal.importer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.ActivityImporter;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.integration.resource.Resource;
import com.github.rasifix.trainings.model.Activity;

public class DefaultActivityImporter implements ActivityImporter {

	private List<Format> formats = new LinkedList<Format>();

	public void addFormat(Format format) {
		synchronized (formats) {
			this.formats.add(format);
		}
	}
	
	public void removeFormat(Format format) {
		synchronized (formats) {
			this.formats.remove(format);
		}
	}
	
	@Override
	public List<Activity> importActivities(Resource resource) throws IOException {
		Format[] services;
		synchronized (this) {
			services = formats.toArray(new Format[formats.size()]);
		}
		
		for (Format format : services) {
			try {
				if (format.canRead(resource)) {
					return format.createReader().readActivities(resource.openInputStream());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new LinkedList<Activity>();
	}

}
