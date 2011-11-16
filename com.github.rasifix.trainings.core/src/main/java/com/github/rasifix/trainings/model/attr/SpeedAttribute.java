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

import javax.measure.Measure;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import com.github.rasifix.trainings.model.TrackpointAttribute;


public class SpeedAttribute implements TrackpointAttribute {

	private final Measure<Velocity> speed;

	public SpeedAttribute(double speedInMetersPerSecond) {
		this(Measure.valueOf(speedInMetersPerSecond, SI.METERS_PER_SECOND));
	}
	
	public SpeedAttribute(Measure<Velocity> speed) {
		this.speed = speed;
	}
	
	@Override
	public Double getValue() {
		return speed.doubleValue(SI.METERS_PER_SECOND);
	}

}
