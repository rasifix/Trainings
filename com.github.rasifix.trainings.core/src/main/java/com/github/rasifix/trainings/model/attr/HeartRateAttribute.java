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
package com.github.rasifix.trainings.model.attr;

import com.github.rasifix.trainings.model.AttributeSummaryBuilder;
import com.github.rasifix.trainings.model.TrackpointAttribute;


public class HeartRateAttribute implements TrackpointAttribute {

	private final int heartRate;

	public HeartRateAttribute(int beatsPerMinute) {
		this.heartRate = beatsPerMinute;
	}
	
	@Override
	public Integer getValue() {
		return heartRate;
	}
	
	public static AttributeSummaryBuilder<AvgMaxSummary> getDefaultSummaryBuilder() {
		return new AvgMaxSummaryBuilder(HeartRateAttribute.class);
	}

}
