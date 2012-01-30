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
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.format.tcx.internal.TcxReader;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxLap;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxPosition;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrack;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrackpoint;
import com.github.rasifix.trainings.format.tcx.internal.processors.TcxActivityProcessorChain;
import com.github.rasifix.trainings.format.tcx.internal.processors.TcxLapCleanup;
import com.github.rasifix.trainings.format.tcx.internal.processors.TcxTrackCleanup;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class TcxActivityReader implements ActivityReader {

	@Override
	public List<Activity> readActivities(InputStream inputStream) throws IOException {
		TcxReader reader = new TcxReader();
		List<TcxActivity> activities = reader.read(inputStream);
		
		TcxActivityProcessorChain chain = new TcxActivityProcessorChain();
		chain.addProcessor(new TcxTrackCleanup());
		chain.addProcessor(new TcxLapCleanup());
		
		final List<Activity> result = new LinkedList<Activity>();
		for (final TcxActivity activity : activities) {
			chain.process(activity);		
			result.add(convert(activity));
		}
		
		return result;
	}
	
	protected Activity convert(final TcxActivity source) {
		Date Date = getDate(source.getId());
		Activity activity = new Activity(Date);
		String sport = source.getSport();
		
		Track current = new Track(Date);
		current.setSport(sport);
		
		activity.addTrack(current);
		
		TcxTrack previous = null;
		for (final TcxLap lap : source.getLaps()) {
			for (final TcxTrack track : lap.getTracks()) {
				if (hasGap(previous, track)) {
					current = new Track(new Date(track.getFirstTrackpoint().getTime().getTime()));
					current.setSport(sport);
					activity.addTrack(current);
				}
				
				for (final TcxTrackpoint trackpoint : track.getTrackpoints()) {
					final Trackpoint converted = convert(current, trackpoint);
					if (converted.getAttributeCount() != 0) {
						current.addTrackpoint(converted);
					}
				}
				
				previous = track;
			}
		}
		
		return activity;
	}

	private Date getDate(String value) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			return format.parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private boolean hasGap(TcxTrack t1, TcxTrack t2) {
		if (t1 != null) {
			TcxTrackpoint tp1 = t1.getLastTrackpoint();
			TcxTrackpoint tp2 = t2.getFirstTrackpoint();
			long diff = tp2.getTime().getTime() - tp1.getTime().getTime();
			return diff > 5000;
		}
		return false;
	}
	
	private Trackpoint convert(Track track, TcxTrackpoint source) {
		long elapsedTime = source.getTime().getTime() - track.getStartTime().getTime();
		
		Trackpoint result = new Trackpoint(elapsedTime);
		
		if (source.getDistance() != null) {
			result.addAttribute(new DistanceAttribute(source.getDistance()));
		}
		
		if (source.getAltitude() != null) {
			result.addAttribute(new AltitudeAttribute(source.getAltitude()));
		}
		
		if (source.getPosition() != null) {
			result.addAttribute(new PositionAttribute(convert(source.getPosition())));
		}
		
		if (source.getHeartRate() != null) {
			result.addAttribute(new HeartRateAttribute(source.getHeartRate()));
		}
		
		if (source.getBikeCadence() != null) {
			result.addAttribute(new CadenceAttribute(source.getBikeCadence()));
			
		} else if (source.getRunCadence() != null) {
			result.addAttribute(new CadenceAttribute(source.getRunCadence()));
		}
		
		if (source.getSpeed() != null) {
			result.addAttribute(new SpeedAttribute(source.getSpeed()));
		}
		
		return result;
	}
	
	private Position convert(TcxPosition source) {
		return source != null 
		     ? new Position(source.getLatitude(), source.getLongitude())
		     : null;
	}

}
