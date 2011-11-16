/*
 * Copyright 2011 Simon Raess
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rasifix.trainings.internal.exporter;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.integration.transformer.Transformer;
import com.github.rasifix.trainings.model.Activity;


public class ExportEngine implements Transformer {
	
	private List<ActivityExporter> exporters = new LinkedList<ActivityExporter>();

	public synchronized void addExporter(ActivityExporter exporter) {
		this.exporters.add(exporter);
	}
	
	public synchronized void removeExporter(ActivityExporter exporter) {
		this.exporters.remove(exporter);
	}
	
	@Override
	public Object transform(Object input) {
		ActivityExporter[] services;
		
		synchronized (this) {
			services = exporters.toArray(new ActivityExporter[exporters.size()]);
		}
		
		List<URL> urls = new LinkedList<URL>();
		Activity activity = (Activity) input;
		for (ActivityExporter exporter : services) {
			try {
				urls.add(exporter.exportActivity(activity));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return urls;
	}
	
}
