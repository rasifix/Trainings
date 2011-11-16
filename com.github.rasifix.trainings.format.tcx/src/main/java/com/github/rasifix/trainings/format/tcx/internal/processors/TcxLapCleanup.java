/* 
 * Copyright 2010 Simon Raess
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
package com.github.rasifix.trainings.format.tcx.internal.processors;

import java.util.Iterator;

import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxLap;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrack;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrackpoint;


/**
 * Cleanup task for laps of an activity.
 * 
 * <ul>
 *  <li>removes laps that do not contain any tracks</li>
 *  <li>adds a trackpoint for the lap start time (if none exists)</li>
 * </ul>
 */
public class TcxLapCleanup implements TcxActivityProcessor {

	@Override
	public void process(final TcxActivity activity) {
		final Iterator<TcxLap> it = activity.getLaps().iterator();
		while (it.hasNext()) {
			final TcxLap lap = it.next();
			if (lap.getTrackCount() == 0) {
				it.remove();
			} else {
				insertLapStartTrackpoint(lap);
			}
		}
	}

	private void insertLapStartTrackpoint(TcxLap lap) {
		final TcxTrack track = lap.getTrack(0);
		final TcxTrackpoint trackpoint = track.getTrackpoint(0);
		if (lap.getStartTime().before(trackpoint.getTime())) {
			final TcxTrackpoint lapStart = new TcxTrackpoint();
			lapStart.setTime(lap.getStartTime());
			track.addTrackpoint(0, lapStart);
		}
	}

}
