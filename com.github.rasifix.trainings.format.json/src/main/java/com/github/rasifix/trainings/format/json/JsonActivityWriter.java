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
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.rasifix.saj.JsonOutputHandler;
import com.github.rasifix.saj.JsonWriter;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.trainings.format.ActivityWriter;
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
		
		writer.startMember("tracks");
		output(writer, activity.getTracks());
		writer.endMember();
		
		writer.endObject();
	}
	
	private void output(final JsonOutputHandler writer, final List<Track> tracks) {
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
		
		outputTrackSummary(writer, track);
		
		writer.startMember("trackpoints");
		writer.startArray();
		for (final Trackpoint trackpoint : track.getTrackpoints()) {
			output(writer, trackpoint);
		}
		writer.endArray();
		writer.endMember();
		
		writer.endObject();
	}

	private void outputTrackSummary(final JsonOutputHandler writer, final Track track) {
		writer.startMember("summary");
		writer.startObject();
		
		if (track.getSport() != null) {
			writer.member("sport", track.getSport());
		}
		
		if (track.getTotalTimeInSeconds() != 0.0) {
			writer.member("totalTime", Math.round(track.getTotalTimeInSeconds()));
		}
		
		if (track.getDistance() != null) {
			writer.member("distance", track.getDistance(), DISTANCE_PRECISION);
		}
		
		if (track.getSpeed() != null) {
			writer.member("speed", track.getSpeed(), SPEED_PRECISION);
		}
		
		if (track.getAverageHeartRate() != null) {
			writer.member("avgHr", roundToInt(track.getAverageHeartRate()));
		}
		
		writer.member("altGain", track.getAltitudeGain(), ALTITUDE_PRECISION);
		writer.member("altLoss", track.getAltitudeLoss(), ALTITUDE_PRECISION);

		writer.endObject();
		writer.endMember();
	}

	private static final int roundToInt(double average) {
		return (int) Math.round(average);
	}

	private void output(final JsonOutputHandler writer, final Trackpoint trackpoint) {
		writer.startObject();
		
		writer.member("elapsed", trackpoint.getElapsedTime());
		
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
			Double cadence = trackpoint.getAttribute(CadenceAttribute.class).getValue();
			writer.member("cadence", roundToInt(cadence));
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
	
}
