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
package com.github.rasifix.trainings.internal.processors;

import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.ActivityProcessor;
import com.github.rasifix.trainings.integration.transformer.Transformer;
import com.github.rasifix.trainings.model.ActivityImpl;


public class ProcessorEngine implements Transformer {

	private List<ActivityProcessor> processors = new LinkedList<ActivityProcessor>();

	public synchronized void addProcessor(ActivityProcessor processor) {
		this.processors.add(processor);
	}

	public synchronized void removeProcessor(ActivityProcessor processor) {
		this.processors.remove(processor);
	}
	
	@Override
	public Object transform(Object input) {
		ActivityImpl activity = (ActivityImpl) input;
		
		ActivityProcessor[] copy;
		synchronized (this) {
			copy = processors.toArray(new ActivityProcessor[processors.size()]);
		}
		
		for (ActivityProcessor processor : copy) {
			activity = processor.processActivity(activity);
		}
		
		return activity;
	}


}
