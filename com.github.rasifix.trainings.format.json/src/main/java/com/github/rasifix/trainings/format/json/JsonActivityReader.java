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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.ActivityImpl;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.TracklessActivity;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PlaceNameAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.PowerAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class JsonActivityReader implements ActivityReader {

	@Override
	public List<Activity> readActivities(InputStream inputStream) throws IOException {
		return Collections.singletonList(readActivity(inputStream));
	}

	public Activity readActivity(Reader reader) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(reader);
		return readActivity((ObjectNode) json);
	}
	
	public Activity readActivity(InputStream inputStream) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(inputStream);
		return readActivity((ObjectNode) json);
	}

	public Activity readActivity(ObjectNode json) {
		String type = json.path("type").asText();
		if ("training".equals(type)) {
			return readSimpleActivity(json);
		}
		if (!"activity".equals(type)) {
			throw new IllegalArgumentException("json input is not an activity");
		}
		
		String id = json.path("_id").asText();
		String rev = json.path("_rev").asText();
		
		ObjectNode activityNode = (ObjectNode) json.get("activity");

		Date startTime = parse(activityNode.path("date").asText());

		ActivityImpl activity = new ActivityImpl(startTime);
		activity.setId(id);
		activity.setRevision(rev);

		ArrayNode equipments = (ArrayNode) activityNode.path("equipments");
		if (equipments != null) {
			for (JsonNode jsonEquipment : equipments) {
				Equipment equipment = new Equipment();
				equipment.setId(jsonEquipment.path("id").asText());
				equipment.setName(jsonEquipment.path("name").asText());
				equipment.setBrand(jsonEquipment.path("brand").asText());
				equipment.setDateOfPurchase(parseDate(jsonEquipment.path("dateOfPurchase").asText()));
				activity.addEquipment(equipment);
			}
		}
		
		ArrayNode tracks = (ArrayNode) activityNode.path("tracks");
		for (JsonNode jsonTrack : tracks) {
			Date trackStart = parse(jsonTrack.path("startTime").asText());

			Track track = new Track(trackStart);
			track.setSport(jsonTrack.path("sport").asText());

			ArrayNode trackpoints = (ArrayNode) jsonTrack.path("trackpoints");
			for (JsonNode jsonTrackpoint : trackpoints) {
				long elapsedTime = (long) jsonTrackpoint.path("elapsed").asDouble();
				Trackpoint trackpoint = new Trackpoint(elapsedTime);

				if (jsonTrackpoint.has("pos")) {
					JsonNode jsonPos = jsonTrackpoint.path("pos");
					trackpoint.addAttribute(new PositionAttribute(jsonPos
							.path("lat").asDouble(), jsonPos.path("lng").asDouble()));
				}

				if (jsonTrackpoint.has("alt")) {
					trackpoint.addAttribute(new AltitudeAttribute(
							jsonTrackpoint.path("alt").asDouble()));
				}

				if (jsonTrackpoint.has("hr")) {
					trackpoint.addAttribute(new HeartRateAttribute(
							jsonTrackpoint.path("hr").asInt()));
				}

				if (jsonTrackpoint.has("distance")) {
					trackpoint.addAttribute(new DistanceAttribute(
							jsonTrackpoint.path("distance").asDouble()));
				}

				if (jsonTrackpoint.has("speed")) {
					trackpoint.addAttribute(new SpeedAttribute(jsonTrackpoint
							.path("speed").asDouble()));
				}

				if (jsonTrackpoint.has("power")) {
					trackpoint.addAttribute(new PowerAttribute(jsonTrackpoint
							.path("power").asDouble()));
				}

				if (jsonTrackpoint.has("cadence")) {
					trackpoint.addAttribute(new CadenceAttribute(jsonTrackpoint
							.path("cadence").asInt()));
				}
				
				if (jsonTrackpoint.has("place")) {
					trackpoint.addAttribute(new PlaceNameAttribute(jsonTrackpoint.path("place").asText()));
				}

				track.addTrackpoint(trackpoint);
			}

			activity.addTrack(track);
		}

		return activity;
	}

	private Activity readSimpleActivity(ObjectNode json) {
		String id = json.path("id").asText();
		String sport = json.path("sport").asText();
		int duration = json.path("duration").asInt();
		int distance = json.path("distance").asInt();
		Date startTime = parse(json.path("date").asText());
		
		TracklessActivity activity = new TracklessActivity(startTime);
		activity.setId(id);
		activity.setSport(sport);
		activity.setDuration(duration);
		activity.setDistance(distance);
		return activity;
	}

	private Date parse(String text) {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
