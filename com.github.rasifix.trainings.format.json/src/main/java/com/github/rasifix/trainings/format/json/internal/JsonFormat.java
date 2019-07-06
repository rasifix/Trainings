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
package com.github.rasifix.trainings.format.json.internal;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.format.json.JsonActivityReader;
import com.github.rasifix.trainings.format.json.JsonActivityWriter;
import com.github.rasifix.trainings.integration.resource.Resource;

@Component(properties={ "com.github.rasifix.trainings.format=json" })
public class JsonFormat implements Format {

	@Override
	public boolean canRead(Resource resource) {
		final String filename = resource.getName().toLowerCase();
		return filename.endsWith(".json") || filename.endsWith(".activity");
	}

	@Override
	public ActivityReader createReader() {
		return new JsonActivityReader();
	}
	
	@Override
	public boolean canWrite() {
		return true;
	}

	@Override
	public ActivityWriter createWriter() {
		return new JsonActivityWriter();
	}

}
