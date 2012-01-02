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
package com.github.rasifix.trainings.format.tcx;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.format.tcx.internal.TcxWriter;
import com.github.rasifix.trainings.format.tcx.internal.model.Intensity;
import com.github.rasifix.trainings.format.tcx.internal.model.SensorState;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxLap;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxPosition;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrack;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrackpoint;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class TcxActivityWriter implements ActivityWriter {

	@Override
	public void writeActivity(Activity activity, OutputStream outputStream) throws IOException {
		TcxActivity tcxActivity = convert(activity);
		TcxWriter writer = new TcxWriter();
		writer.output(Collections.singletonList(tcxActivity), outputStream);
	}

	private static TcxActivity convert(Activity activity) {
		TcxActivity tcxActivity = new TcxActivity(generateId(activity.getStartTime()), getSport(activity));
		
		for (Track track : activity.getTracks()) {
			TcxLap tcxLap = new TcxLap(track.getStartTime());
			tcxLap.setDistance(track.getDistance());
			tcxLap.setIntensity(Intensity.Active);
			tcxLap.setTotalTime(track.getTotalTime());
			tcxLap.setAverageHeartRate(track.getAverageHeartRate().intValue());
			
			tcxActivity.addLap(tcxLap);
			
			TcxTrack tcxTrack = new TcxTrack();
			tcxLap.addTrack(tcxTrack);
			
			for (Trackpoint trackpoint : track.getTrackpoints()) {
				TcxTrackpoint tcxTrackpoint = new TcxTrackpoint();
				tcxTrack.addTrackpoint(tcxTrackpoint);
				
				tcxTrackpoint.setTime(trackpoint.getTime());
				tcxTrackpoint.setSensorState(SensorState.Absent);
				
				if (trackpoint.hasAttribute(PositionAttribute.class)) {
					Position position = trackpoint.getAttribute(PositionAttribute.class).getValue();
					TcxPosition tcxPosition = new TcxPosition(position.getLatitude(), position.getLongitude());
					tcxTrackpoint.setPosition(tcxPosition);
				}
				
				if (trackpoint.hasAttribute(AltitudeAttribute.class)) {
					Double altitude = trackpoint.getAttribute(AltitudeAttribute.class).getValue();
					tcxTrackpoint.setAltitude(altitude);
				}
				
				if (trackpoint.hasAttribute(DistanceAttribute.class)) {
					Double distance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
					tcxTrackpoint.setDistance(distance);
				}
				
				if (trackpoint.hasAttribute(HeartRateAttribute.class)) {
					Integer heartRate = trackpoint.getAttribute(HeartRateAttribute.class).getValue();
					tcxTrackpoint.setHeartRate(heartRate);
				}
				
				if (trackpoint.hasAttribute(SpeedAttribute.class)) {
					Double speed = trackpoint.getAttribute(SpeedAttribute.class).getValue();
					tcxTrackpoint.setSpeed(speed);
				}
			}
		}
		return tcxActivity;
	}

	private static String generateId(Date startTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(startTime);
	}

	private static String getSport(Activity activity) {
		return activity.getTrack(0).getSport();
	}

}
