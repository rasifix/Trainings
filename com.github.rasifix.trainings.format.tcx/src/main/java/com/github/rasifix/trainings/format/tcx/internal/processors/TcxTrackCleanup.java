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
 * Cleanup task for tracks of a lap.
 * 
 * <ul>
 *  <li>merges two tracks within a lap if they are continuous</li>
 *  <li>removes empty tracks, i.e. tracks with 2 or less trackpoints</li>
 * </ul>
 */
public class TcxTrackCleanup implements TcxActivityProcessor {

	private static final int ADJACENCY_TOLERANCE = 5000;

	@Override
	public void process(final TcxActivity activity) {
		for (final TcxLap lap : activity.getLaps()) {
			mergeContinuousTracks(lap);			
			removeEmptyTracks(lap);
		}
	}

	private void mergeContinuousTracks(final TcxLap lap) {
		for (int i = 0; i < lap.getTracks().size() - 1; i++) {
			final TcxTrack t1 = lap.getTrack(i);
			final TcxTrack t2 = lap.getTrack(i + 1);
			if (isContinuous(t1, t2)) {
				mergeTracks(t1, t2);
				lap.removeTrack(i + 1);
				i -= 1;
			}
		}
	}

	private void removeEmptyTracks(TcxLap lap) {
		Iterator<TcxTrack> it = lap.getTracks().iterator();
		while (it.hasNext()) {
			TcxTrack track = it.next();
			if (track.getTrackpointCount() <= 2) {
				it.remove();
			}
		}
	}

	private void mergeTracks(final TcxTrack t1, final TcxTrack t2) {
		final int last = t1.getTrackpoints().size() - 1;
		final TcxTrackpoint tp1 = t1.getTrackpoints().get(last);
		if (tp1.getPosition() == null) {
			t1.removeTrackpoint(last);
		}
		
		final TcxTrackpoint tp2 = t2.getTrackpoints().get(0);
		if (tp2.getPosition() == null) {
			t2.removeTrackpoint(0);
		}
		
		t1.addTrackpoints(t2.getTrackpoints());
	}

	/**
	 * Checks whether the second track directly continues the first track.
	 * In some cases tracks are unnecessarily split. 
	 * 
	 * @param t1 the earlier track
	 * @param t2 the successor track
	 * @return true if the tracks are directly adjacent
	 */
	private boolean isContinuous(final TcxTrack t1, final TcxTrack t2) {
		// safeguard - empty tracks should be removed anyways
		if (t1.getTrackpoints().isEmpty() || t2.getTrackpoints().isEmpty()) {
			return false;
		}
		
		final TcxTrackpoint tp1 = t1.getTrackpoint(t1.getTrackpoints().size() - 1);
		final TcxTrackpoint tp2 = t2.getTrackpoint(0);
		long diff = tp2.getTime().getTime() - tp1.getTime().getTime();
		
		return diff < ADJACENCY_TOLERANCE;
	}

}
