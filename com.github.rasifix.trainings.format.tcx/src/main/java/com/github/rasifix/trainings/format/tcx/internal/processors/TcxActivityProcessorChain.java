/* 
 * Copyright 2010 Simon Raess
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
package com.github.rasifix.trainings.format.tcx.internal.processors;

import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;


/**
 * Chain of TcxActivityProcessors. Processors can be added by invoking {@link #addProcessor(TcxActivityProcessor)}.
 * The chain calls those in order they have been added.
 */
public class TcxActivityProcessorChain implements TcxActivityProcessor {

	private final List<TcxActivityProcessor> processors = new LinkedList<TcxActivityProcessor>();
	
	public void addProcessor(final TcxActivityProcessor processor) {
		this.processors.add(processor);
	}
	
	public void process(final TcxActivity activity) {
		for (final TcxActivityProcessor processor : processors) {
			processor.process(activity);
		}
	}

}
