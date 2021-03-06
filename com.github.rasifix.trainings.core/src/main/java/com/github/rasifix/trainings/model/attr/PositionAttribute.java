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

import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.TrackpointAttribute;

public class PositionAttribute implements TrackpointAttribute {
	
	private final Position position;
	
	public PositionAttribute(double latitude, double longitude) {
		this.position = new Position(latitude, longitude);
	}
	
	public PositionAttribute(Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	@Override
	public Position getValue() {
		return position;
	}
	
	@Override
	public String toString() {
		return "@Position" + position;
	}
	
}
