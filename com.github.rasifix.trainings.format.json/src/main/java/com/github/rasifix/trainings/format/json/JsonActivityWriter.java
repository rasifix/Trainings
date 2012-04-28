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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.rasifix.saj.JsonOutputHandler;
import com.github.rasifix.saj.JsonWriter;
import com.github.rasifix.saj.dom.JsonModelBuilder;
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> writeActivity(Activity activity) {
		JsonModelBuilder builder = new JsonModelBuilder();
		writeActivity(activity, builder);
		return (Map<String, Object>) builder.getResult();
	}
	
	public void writeActivity(Activity activity, Writer out) throws IOException {
		JsonWriter writer = new JsonWriter(out);
		writer.setPrettyPrint(false);
		writeActivity(activity, writer);
		writer.close();
	}
	
	protected void writeActivity(Activity activity, JsonOutputHandler writer) {
		writer.startObject();
		
		writer.member("date", format(activity.getStartTime()));
		
		if (activity.getSport() != null) {
			writer.member("sport", activity.getSport().toUpperCase());
		}
		
		outputSummary(writer, activity);
		
		writer.startMember("equipments");
		outputEquipments(writer, activity.getEquipments());
		writer.endMember();
		
		writer.startMember("tracks");
		outputTracks(writer, activity.getTracks());
		writer.endMember();
		
		writer.endObject();
	}
	
	private void outputSummary(JsonOutputHandler writer, HasSummary summary) {
		writer.startMember("summary");
		writer.startObject();
		
		if (summary.getSport() != null) {
			writer.member("sport", summary.getSport());
		}
		
		if (summary.getDuration() != 0) {
			writer.member("totalTime", Math.round(summary.getDuration()));
		}
		
		if (summary.getDistance() != 0) {
			writer.member("distance", summary.getDistance());
		}
		
		if (summary.getSpeed() != null) {
			writer.member("speed", summary.getSpeed(), SPEED_PRECISION);
		}
		
		if (!summary.getPlaces().isEmpty()) {
			writer.startMember("places");
			writer.startArray();
			for (String place : summary.getPlaces()) {
				writer.value(place);
			}
			writer.endArray();
			writer.endMember();
		}

		AvgMaxSummary hrSummary = summary.getSummary(HeartRateAttribute.getDefaultSummaryBuilder());
		if (hrSummary != null) {
			writer.startMember("hr");
			writeAvgMaxSummary(writer, hrSummary);
			writer.endMember();
		}
		
		AvgMaxSummary cadenceSummary = summary.getSummary(CadenceAttribute.getDefaultSummaryBuilder());
		if (cadenceSummary != null) {
			writer.startMember("cadence");
			writeAvgMaxSummary(writer, cadenceSummary);
			writer.endMember();
		}
		
		AltitudeSummary altSummary = summary.getSummary(AltitudeAttribute.getDefaultSummaryBuilder());
		if (altSummary != null) {
			writer.startMember("alt");
			outputAltitudeSummary(writer, altSummary);
			writer.endMember();
		}

		writer.endObject();
		writer.endMember();
	}

	private void outputAltitudeSummary(JsonOutputHandler writer, AltitudeSummary altSummary) {
		writer.startObject();
		writer.member("min", altSummary.getMin());
		writer.member("avg", altSummary.getAvg());
		writer.member("max", altSummary.getMax());
		writer.member("gain", altSummary.getAltGain());
		writer.member("loss", altSummary.getAltLoss());
		writer.member("start", altSummary.getStartAltitude());
		writer.member("end", altSummary.getEndAltitude());
		writer.endObject();
	}

	private void writeAvgMaxSummary(JsonOutputHandler writer, AvgMaxSummary summary) {
		writer.startObject();
		writer.member("avg", summary.getAvg());
		writer.member("max", summary.getMax());
		writer.endObject();
	}

	private void outputEquipments(JsonOutputHandler writer, Collection<Equipment> equipments) {
		writer.startArray();
		for (Equipment equipment : equipments) {
			writer.startObject();
			writer.member("id", equipment.getId());
			writer.member("name", equipment.getName());
			writer.member("brand", equipment.getBrand());
			writer.member("dateOfPurchase", formatDate(equipment.getDateOfPurchase()));
			writer.endObject();
		}
		writer.endArray();
	}
	
	private void outputTracks(final JsonOutputHandler writer, final List<Track> tracks) {
		writer.startArray();
		
		for (final Track track : tracks) {
			output(writer, track);
		}
		
		writer.endArray();
	}

	private void output(final JsonOutputHandler writer, final Track track) {
		writer.startObject();
		
		writer.member("startTime", format(track.getStartTime()));
		
		if (track.getSport() != null) {
			writer.member("sport", track.getSport());
		}
		
		outputSummary(writer, track);
		
		writer.startMember("trackpoints");
		writer.startArray();
		for (final Trackpoint trackpoint : track.getTrackpoints()) {
			outputTrackpoint(writer, trackpoint);
		}
		writer.endArray();
		writer.endMember();
		
		writer.endObject();
	}

	private void outputTrackpoint(final JsonOutputHandler writer, final Trackpoint trackpoint) {
		writer.startObject();
		
		writer.member("elapsed", trackpoint.getElapsedTime());
		
		if (trackpoint instanceof LapPoint) {
			writer.member("type", "lappoint");
		}
		
		if (trackpoint.hasAttribute(PositionAttribute.class)) {
			writer.startMember("pos");
			writer.startObject();
			writer.member("lat", trackpoint.getPosition().getLatitude(), LAT_LON_PRECISION);
			writer.member("lng", trackpoint.getPosition().getLongitude(), LAT_LON_PRECISION);
			writer.endObject();
			writer.endMember();
		}
		
		if (trackpoint.hasAttribute(AltitudeAttribute.class)) {
			Double altitude = trackpoint.getAttribute(AltitudeAttribute.class).getValue();
			writer.member("alt", altitude, ALTITUDE_PRECISION);
		}
		
		if (trackpoint.hasAttribute(HeartRateAttribute.class)) {
			writer.member("hr", trackpoint.getAttribute(HeartRateAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(DistanceAttribute.class)) {
			Double distance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
			writer.member("distance", distance, DISTANCE_PRECISION);
		}
		
		if (trackpoint.hasAttribute(SpeedAttribute.class)) {
			Double speed = trackpoint.getAttribute(SpeedAttribute.class).getValue();
			writer.member("speed", speed, SPEED_PRECISION);
		}
		
		if (trackpoint.hasAttribute(PowerAttribute.class)) {
			Double power = trackpoint.getAttribute(PowerAttribute.class).getValue();
			writer.member("power", power, POWER_PRECISION);
		}
		
		if (trackpoint.hasAttribute(CadenceAttribute.class)) {
			Integer cadence = trackpoint.getAttribute(CadenceAttribute.class).getValue();
			writer.member("cadence", cadence);
		}
		
		if (trackpoint.hasAttribute(PlaceNameAttribute.class)) {
			String placeName = trackpoint.getAttribute(PlaceNameAttribute.class).getValue();
			writer.member("place", placeName);
		}
		
		writer.endObject();
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
