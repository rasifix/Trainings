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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.model.HasSummary;
import com.github.rasifix.trainings.model.LapPoint;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.AltitudeSummary;
import com.github.rasifix.trainings.model.attr.AvgMaxSummary;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PlaceNameAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.PowerAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class JsonActivityWriter implements ActivityWriter {

	private static final int POWER_PRECISION = 1;
	private static final int SPEED_PRECISION = 2;
	private static final int DISTANCE_PRECISION = 1;
	private static final int ALTITUDE_PRECISION = 1;
	private static final int LAT_LON_PRECISION = 6;

	@Override
	public void writeActivity(Activity activity, OutputStream outputStream) throws IOException {
		final OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writeActivity(activity, writer);
	}
	
	public ObjectNode writeActivity(Activity activity) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		writeActivity(activity, node, mapper);
		return node;
	}
	
	public void writeActivity(Activity activity, Writer out) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		writeActivity(activity, node, mapper);
		mapper.writeValue(out, node);
	}
	
	protected void writeActivity(Activity activity, ObjectNode node, ObjectMapper mapper) {
		node.put("date", format(activity.getStartTime()));
		
		if (activity.getSport() != null) {
			node.put("sport", activity.getSport().toUpperCase());
		}
		
		node.set("summary", outputSummary(activity, mapper));
		node.set("equipments", outputEquipments(activity.getEquipments(), mapper));		
		node.set("tracks", outputTracks(activity.getTracks(), mapper));
	}
	
	private ObjectNode outputSummary(HasSummary summary, ObjectMapper mapper) {
		ObjectNode node = mapper.createObjectNode();
		
		if (summary.getSport() != null) {
			node.put("sport", summary.getSport());
		}
		
		if (summary.getDuration() != 0) {
			node.put("totalTime", Math.round(summary.getDuration()));
		}
		
		if (summary.getDistance() != 0) {
			node.put("distance", summary.getDistance());
		}
		
		if (summary.getSpeed() != null) {
			node.put("speed", format(summary.getSpeed(), SPEED_PRECISION));
		}
		
		if (!summary.getPlaces().isEmpty()) {
			ArrayNode places = mapper.createArrayNode();
			for (String place : summary.getPlaces()) {
				places.add(place);
			}
			node.set("places", places);
		}

		AvgMaxSummary hrSummary = summary.getSummary(HeartRateAttribute.getDefaultSummaryBuilder());
		if (hrSummary != null) {
			node.set("hr", writeAvgMaxSummary(hrSummary, mapper));
		}
		
		AvgMaxSummary cadenceSummary = summary.getSummary(CadenceAttribute.getDefaultSummaryBuilder());
		if (cadenceSummary != null) {
			node.set("cadence", writeAvgMaxSummary(cadenceSummary, mapper));
		}
		
		AltitudeSummary altSummary = summary.getSummary(AltitudeAttribute.getDefaultSummaryBuilder());
		if (altSummary != null) {
			node.set("alt", outputAltitudeSummary(altSummary, mapper));
		}
		
		return node;
	}

	private ObjectNode outputAltitudeSummary(AltitudeSummary altSummary, ObjectMapper mapper) {
		ObjectNode result = mapper.createObjectNode();
		result.put("min", altSummary.getMin());
		result.put("avg", altSummary.getAvg());
		result.put("max", altSummary.getMax());
		result.put("gain", altSummary.getAltGain());
		result.put("loss", altSummary.getAltLoss());
		result.put("start", altSummary.getStartAltitude());
		result.put("end", altSummary.getEndAltitude());
		return result;
	}

	private ObjectNode writeAvgMaxSummary(AvgMaxSummary summary, ObjectMapper mapper) {
		ObjectNode result = mapper.createObjectNode();
		result.put("avg", summary.getAvg());
		result.put("max", summary.getMax());
		return result;
	}

	private ArrayNode outputEquipments(Collection<Equipment> equipments, ObjectMapper mapper) {
		ArrayNode result = mapper.createArrayNode();
		for (Equipment equipment : equipments) {
			result.add(outputEquipment(equipment, mapper));
		}
		return result;
	}
	
	private ObjectNode outputEquipment(Equipment equipment, ObjectMapper mapper) {
		ObjectNode result = mapper.createObjectNode();
		result.put("id", equipment.getId());
		result.put("name", equipment.getName());
		result.put("brand", equipment.getBrand());
		result.put("dateOfPurchase", formatDate(equipment.getDateOfPurchase()));
		return result;
	}
	
	private ArrayNode outputTracks(final List<Track> tracks, ObjectMapper mapper) {
		ArrayNode result = mapper.createArrayNode();
		
		for (final Track track : tracks) {
			result.add(outputTrack(track, mapper));
		}
		
		return result;
	}

	private ObjectNode outputTrack(final Track track, ObjectMapper mapper) {
		ObjectNode result = mapper.createObjectNode();
		
		result.put("startTime", format(track.getStartTime()));
		
		if (track.getSport() != null) {
			result.put("sport", track.getSport());
		}
		
		result.set("summary", outputSummary(track, mapper));
		result.set("trackpoints", outputTrackpoints(track.getTrackpoints(), mapper));
		
		return result;
	}
	
	private ArrayNode outputTrackpoints(final List<Trackpoint> trackpoints, ObjectMapper mapper) {
		ArrayNode result = mapper.createArrayNode();
		for (final Trackpoint trackpoint : trackpoints) {
			result.add(outputTrackpoint(trackpoint, mapper));
		}
		return result;
	}

	private ObjectNode outputTrackpoint(final Trackpoint trackpoint, ObjectMapper mapper) {
		ObjectNode node = mapper.createObjectNode();
		
		node.put("elapsed", trackpoint.getElapsedTime());
		
		if (trackpoint instanceof LapPoint) {
			node.put("type", "lappoint");
		}
		
		if (trackpoint.hasAttribute(PositionAttribute.class)) {
			ObjectNode positionNode = mapper.createObjectNode(); 
			positionNode.put("lat", format(trackpoint.getPosition().getLatitude(), LAT_LON_PRECISION));
			positionNode.put("lng", format(trackpoint.getPosition().getLongitude(), LAT_LON_PRECISION));
			node.set("pos", positionNode);
		}
		
		if (trackpoint.hasAttribute(AltitudeAttribute.class)) {
			Double altitude = trackpoint.getAttribute(AltitudeAttribute.class).getValue();
			node.put("alt", format(altitude, ALTITUDE_PRECISION));
		}
		
		if (trackpoint.hasAttribute(HeartRateAttribute.class)) {
			node.put("hr", trackpoint.getAttribute(HeartRateAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(DistanceAttribute.class)) {
			Double distance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
			node.put("distance", format(distance, DISTANCE_PRECISION));
		}
		
		if (trackpoint.hasAttribute(SpeedAttribute.class)) {
			Double speed = trackpoint.getAttribute(SpeedAttribute.class).getValue();
			node.put("speed", format(speed, SPEED_PRECISION));
		}
		
		if (trackpoint.hasAttribute(PowerAttribute.class)) {
			Double power = trackpoint.getAttribute(PowerAttribute.class).getValue();
			node.put("power", format(power, POWER_PRECISION));
		}
		
		if (trackpoint.hasAttribute(CadenceAttribute.class)) {
			Integer cadence = trackpoint.getAttribute(CadenceAttribute.class).getValue();
			node.put("cadence", cadence);
		}
		
		if (trackpoint.hasAttribute(PlaceNameAttribute.class)) {
			String placeName = trackpoint.getAttribute(PlaceNameAttribute.class).getValue();
			node.put("place", placeName);
		}
		
		return node;
	}
	
	private String format(double value, int fractionDigits) {
		DecimalFormat format = new DecimalFormat();
		format.setGroupingUsed(false);
		format.setMaximumFractionDigits(fractionDigits);
		return format.format(value);
	}
	
	private String format(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("cannot format null time");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(time);
	}
	
	private String formatDate(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("cannot format null date");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(time);
	}
	
}
