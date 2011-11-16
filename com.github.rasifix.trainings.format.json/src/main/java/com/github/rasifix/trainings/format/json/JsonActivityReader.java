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
package com.github.rasifix.trainings.format.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.github.rasifix.saj.JsonReader;
import com.github.rasifix.saj.dom.JsonArray;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.PowerAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class JsonActivityReader implements ActivityReader {

	@Override
	public List<Activity> readActivities(InputStream inputStream) throws IOException {
		Activity activity = readActivity(inputStream);
		return Collections.singletonList(activity);
	}

	public Activity readActivity(Reader reader) throws IOException {
		JsonModelBuilder builder = new JsonModelBuilder();
		JsonReader jsonReader = new JsonReader(builder);
		jsonReader.parseJson(reader);
		return readActivity((JsonObject) builder.getResult());
	}
	
	public Activity readActivity(InputStream inputStream) throws IOException {
		JsonModelBuilder builder = new JsonModelBuilder();
		JsonReader reader = new JsonReader(builder);
		reader.parseJson(inputStream);
		return readActivity((JsonObject) builder.getResult());
	}

	public Activity readActivity(JsonObject json) {
		String type = json.getString("type");
		if (!"activity".equals(type)) {
			throw new IllegalArgumentException("json input is not an activity");
		}
		json = json.getObject("activity");

		DateTime startTime = parse(json.getString("date"));

		Activity activity = new Activity(startTime);

		JsonArray tracks = json.getArray("tracks");
		for (int i = 0; i < tracks.size(); i++) {
			JsonObject jsonTrack = tracks.getObject(i);
			DateTime trackStart = parse(jsonTrack.getString("startTime"));

			Track track = new Track(trackStart);

			JsonArray trackpoints = jsonTrack.getArray("trackpoints");
			for (int j = 0; j < trackpoints.size(); j++) {
				JsonObject jsonTrackpoint = trackpoints.getObject(j);

				double elapsedTime = jsonTrackpoint.getDouble("elapsed");
				Trackpoint trackpoint = new Trackpoint(elapsedTime);

				if (jsonTrackpoint.containsKey("pos")) {
					JsonObject jsonPos = jsonTrackpoint.getObject("pos");
					trackpoint.addAttribute(new PositionAttribute(jsonPos
							.getDouble("lat"), jsonPos.getDouble("lng")));
				}

				if (jsonTrackpoint.containsKey("alt")) {
					trackpoint.addAttribute(new AltitudeAttribute(
							jsonTrackpoint.getDouble("alt")));
				}

				if (jsonTrackpoint.containsKey("hr")) {
					trackpoint.addAttribute(new HeartRateAttribute(
							jsonTrackpoint.getInt("hr")));
				}

				if (jsonTrackpoint.containsKey("distance")) {
					trackpoint.addAttribute(new DistanceAttribute(
							jsonTrackpoint.getDouble("distance")));
				}

				if (jsonTrackpoint.containsKey("speed")) {
					trackpoint.addAttribute(new SpeedAttribute(jsonTrackpoint
							.getDouble("speed")));
				}

				if (jsonTrackpoint.containsKey("power")) {
					trackpoint.addAttribute(new PowerAttribute(jsonTrackpoint
							.getDouble("power")));
				}

				if (jsonTrackpoint.containsKey("cadence")) {
					trackpoint.addAttribute(new CadenceAttribute(jsonTrackpoint
							.getInt("cadence")));
				}

				track.addTrackpoint(trackpoint);
			}

			activity.addTrack(track);
		}

		return activity;
	}

	private DateTime parse(String text) {
		final DateTimeFormatter format = DateTimeFormat
				.forPattern("yyyy-MM-dd HH:mm:ss");
		return format.parseDateTime(text);
	}

}
