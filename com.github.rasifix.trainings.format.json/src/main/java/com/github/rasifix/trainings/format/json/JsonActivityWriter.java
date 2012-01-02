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

import com.github.rasifix.saj.JsonContentHandler;
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
	
	protected void writeActivity(Activity activity, JsonContentHandler writer) {
		writer.startObject();
		
		writer.startMember("date");
		writer.value(format(activity.getStartTime()));
		writer.endMember();
		
		writer.startMember("tracks");
		output(writer, activity.getTracks());
		writer.endMember();
		
		writer.endObject();
	}
	
	private void output(final JsonContentHandler writer, final List<Track> tracks) {
		writer.startArray();
		
		for (final Track track : tracks) {
			output(writer, track);
		}
		
		writer.endArray();
	}

	private void output(final JsonContentHandler writer, final Track track) {
		writer.startObject();
		
		member(writer, "startTime", format(track.getStartTime()));
		
		if (track.getSport() != null) {
			writer.startMember("sport");
			writer.value(track.getSport());
			writer.endMember();
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

	private void outputTrackSummary(final JsonContentHandler writer, final Track track) {
		writer.startMember("summary");
		writer.startObject();
		
		if (track.getTotalTime() != null) {
			writer.startMember("totalTime");
			writer.value(track.getTotalTime());
			writer.endMember();
		}
		
		if (track.getDistance() != null) {
			writer.startMember("distance");
			writer.value(track.getDistance());
			writer.endMember();
		}
		
		if (track.getSpeed() != null) {
			writer.startMember("speed");
			writer.value(track.getSpeed());
			writer.endMember();
		}
		
		if (track.getAverageHeartRate() != null) {
			writer.startMember("avgHr");
			writer.value(track.getAverageHeartRate());
			writer.endMember();
		}
		
		writer.startMember("altGain");
		writer.value(track.getAltitudeGain());
		writer.endMember();

		writer.startMember("altLoss");
		writer.value(track.getAltitudeLoss());
		writer.endMember();

		writer.endObject();
		writer.endMember();
	}

	private void output(final JsonContentHandler writer, final Trackpoint trackpoint) {
		writer.startObject();
		
		member(writer, "elapsed", trackpoint.getElapsedTime());
		
		if (trackpoint.hasAttribute(PositionAttribute.class)) {
			writer.startMember("pos");
			writer.startObject();
			member(writer, "lat", trackpoint.getPosition().getLatitude());
			member(writer, "lng", trackpoint.getPosition().getLongitude());
			writer.endObject();
			writer.endMember();
		}
		
		if (trackpoint.hasAttribute(AltitudeAttribute.class)) {
			member(writer, "alt", trackpoint.getAttribute(AltitudeAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(HeartRateAttribute.class)) {
			member(writer, "hr", trackpoint.getAttribute(HeartRateAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(DistanceAttribute.class)) {
			member(writer, "distance", trackpoint.getAttribute(DistanceAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(SpeedAttribute.class)) {
			member(writer, "speed", trackpoint.getAttribute(SpeedAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(PowerAttribute.class)) {
			member(writer, "power", trackpoint.getAttribute(PowerAttribute.class).getValue());
		}
		
		if (trackpoint.hasAttribute(CadenceAttribute.class)) {
			member(writer, "cadence", trackpoint.getAttribute(CadenceAttribute.class).getValue());
		}
		
		writer.endObject();
	}
	
	private void member(JsonContentHandler writer, String member, long value) {
		writer.startMember(member);
		writer.value(value);
		writer.endMember();
	}
	
	private void member(JsonContentHandler writer, String member, double value) {
		writer.startMember(member);
		writer.value(value);
		writer.endMember();
	}
	
	private void member(JsonContentHandler writer, String member, String value) {
		writer.startMember(member);
		writer.value(value);
		writer.endMember();
	}
	
	private String format(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("cannot format null time");
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(time);
	}
	
}
