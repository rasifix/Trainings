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
package com.github.rasifix.trainings.internal.importer;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.integration.resource.Resource;
import com.github.rasifix.trainings.integration.transformer.Transformer;



public class ImportEngine implements Transformer {

	private List<Format> formats = new LinkedList<Format>();
	
	public synchronized void addFormat(Format format) {
		this.formats.add(format);
	}
	
	public synchronized void removeFormat(Format format) {
		this.formats.remove(format);
	}
	
	public Object transform(Object input) {
		Resource resource = (Resource) input;
		
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
		
		return Collections.emptyList();
	}
	
}
