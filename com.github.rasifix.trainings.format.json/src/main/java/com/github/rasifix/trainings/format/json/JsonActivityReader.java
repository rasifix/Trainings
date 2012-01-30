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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.rasifix.saj.JsonReader;
import com.github.rasifix.saj.dom.JsonArray;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;
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
		
		String id = json.getString("_id");
		String rev = json.getString("_rev");
		
		json = json.getObject("activity");

		Date startTime = parse(json.getString("date"));

		Activity activity = new Activity(startTime);
		activity.setId(id);
		activity.setRevision(rev);

		JsonArray equipments = json.getArray("equipments");
		if (equipments != null) {
			for (int i = 0; i < equipments.size(); i++) {
				JsonObject jsonEquipment = equipments.getObject(i);
				Equipment equipment = new Equipment();
				equipment.setId(jsonEquipment.getString("id"));
				equipment.setName(jsonEquipment.getString("name"));
				equipment.setBrand(jsonEquipment.getString("brand"));
				equipment.setDateOfPurchase(parseDate(jsonEquipment.getString("dateOfPurchase")));
				activity.addEquipment(equipment);
			}
		}
		
		JsonArray tracks = json.getArray("tracks");
		for (int i = 0; i < tracks.size(); i++) {
			JsonObject jsonTrack = tracks.getObject(i);
			Date trackStart = parse(jsonTrack.getString("startTime"));

			Track track = new Track(trackStart);
			track.setSport(jsonTrack.getString("sport"));

			JsonArray trackpoints = jsonTrack.getArray("trackpoints");
			for (int j = 0; j < trackpoints.size(); j++) {
				JsonObject jsonTrackpoint = trackpoints.getObject(j);

				long elapsedTime = (long) jsonTrackpoint.getDouble("elapsed");
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

	private Date parse(String text) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setLenient(false);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			throw new IllegalArgumentException("invalid date: " + text);
		}
	}

	private Date parseDate(String text) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			throw new IllegalArgumentException("invalid date: " + text);
		}
	}

}
