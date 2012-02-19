package com.github.rasifix.trainings.format.fit.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.ActivityMesgListener;
import com.garmin.fit.Decode;
import com.garmin.fit.Event;
import com.garmin.fit.EventMesg;
import com.garmin.fit.EventMesgListener;
import com.garmin.fit.EventType;
import com.garmin.fit.File;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.LapMesg;
import com.garmin.fit.LapMesgListener;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.RecordMesgListener;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;
import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.TrackpointSequence;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.model.attr.PowerAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class FitReader implements ActivityReader {

	@Override
	public List<Activity> readActivities(InputStream inputStream) throws IOException {
		Decode decode = new Decode();
		MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
		Listener listener = new Listener();
		
		broadcaster.addListener((FileIdMesgListener) listener);
		
		broadcaster.addListener((ActivityMesgListener) listener);
		broadcaster.addListener((SessionMesgListener) listener);
		broadcaster.addListener((LapMesgListener) listener);
		
		broadcaster.addListener((RecordMesgListener) listener);
		broadcaster.addListener((EventMesgListener) listener);
		
		try {
			broadcaster.run(inputStream);
		} catch (InvalidFileException e) {
			// not an activity file, just return empty list
			return new LinkedList<Activity>();
		}
		
		// import successful, return activity
		return Collections.singletonList(listener.getActivity());
	}
	
	private static Double semitodeg(Integer semicircles) {
		if (semicircles == null) {
			return null;
		}
		return semicircles * (180.0 / Math.pow(2, 31));
	}
	
	protected static class Listener 
			implements FileIdMesgListener, 
			ActivityMesgListener, 
			SessionMesgListener, 
			LapMesgListener,
			RecordMesgListener,
			EventMesgListener,
			StateContext {

		private Activity activity;
		
		private List<Track> tracks = new LinkedList<Track>();
		
		private Track currentTrack;
		
		private State state = new StoppedState();
		
		public Activity getActivity() {
			double distanceCorrection = 0;
			for (int i = 1; i < activity.getTracks().size(); i++) {
				Track current = activity.getTracks().get(i);
				TrackpointSequence trackpoints = current.getTrackpoints().select(DistanceAttribute.class);
				ListIterator<Trackpoint> it = trackpoints.listIterator();
				if (it.hasNext()) {
					Trackpoint first = it.next();
					double startDistance = first.getAttribute(DistanceAttribute.class).getValue();
					while (it.hasNext()) {
						Trackpoint next = it.next();
						double distance = next.getAttribute(DistanceAttribute.class).getValue();
						next.addAttribute(new DistanceAttribute(distance - startDistance - distanceCorrection));
					}
					distanceCorrection += startDistance;
				}
			}
			
			return activity;
		}
		
		@Override
		public void addTrackpoint(Trackpoint trackpoint) {
			this.currentTrack.addTrackpoint(trackpoint);
		}
		
		@Override
		public void startTrack(long timestamp) {
			if (this.activity == null) {
				this.activity = new Activity(timestamp);
			}
			this.currentTrack = new Track(timestamp);
			
			// keep reference to tracks so we can set the sport on session end
			this.tracks.add(currentTrack);
		}
		
		@Override
		public void endTrack(long timestamp) {
			if (currentTrack.getTrackpointCount() >= 2) {
				this.activity.addTrack(currentTrack);
			}
		}
		
		@Override
		public void onMesg(FileIdMesg mesg) {
			// check for correct file type
			if (mesg.getType() != File.ACTIVITY) {
				throw new InvalidFileException(mesg.getType().name());
			}
		}			
		
		@Override
		public void onMesg(ActivityMesg mesg) {
			// available fields
			// - totalTimerTime
		}

		@Override
		public void onMesg(SessionMesg mesg) {
			// available fields (summary, not relevant for us at the moment)
			// - sport
			// - subSport
			// - totalAscent / Descent
			// - totalCalories
			// - totalCycles
			// - totalDistance
			// - totalElapsedTime
			// - totalFatCalories
			// - totalStrides
			// - totalTrainingEffect
			// - totalTimerTime
			// - trigger
			// - trainingStressScore
			// - intensityFactor
			// - leftRightBalance
			// - avg / max cadence / hr / power / runningCadence / speed
			
			// session end does not need to go through the state (we ignore it)
			
			for (Track track : tracks) {
				track.setSport(mesg.getSport().name());
			}
			
			// session ended, new one will follow
			tracks.clear();
		}

		@Override
		public void onMesg(LapMesg mesg) {
			// available fields
			// - avg / max cadence / heartRate / power / runningCadence / speed
			// - endPositionLat / Long
			// - leftRightBalance
			// - intensity
			// - sport
			// - startPositionLat / Long
			// - startTime
			// - totalAscent
			// - totalCalories
			// - totalCycles
			// - totalDescent
			// - totalDistance
			// - totalElapsedTime
			// - totalFatCalories
			// - totalStrides
			// - totalTimerTime
			State next = state.lapEnd(this, mesg);
			updateState(next, mesg.getTimestamp().getDate().getTime());
		}

		@Override
		public void onMesg(RecordMesg mesg) {
			state.record(this, mesg);
		}
		
		private void updateState(State next, long timestamp) {
			if (state != next) {
				state.leave(this, timestamp);
				next.enter(this, timestamp);
				state = next;
			}
		}

		@Override
		public void onMesg(EventMesg mesg) {
			long timestamp = mesg.getTimestamp().getDate().getTime();
			if (mesg.getEvent() == Event.TIMER && mesg.getEventType() == EventType.START) {
				State next = state.startTimer(this, timestamp);
				updateState(next, timestamp);
			} else if (mesg.getEvent() == Event.TIMER && mesg.getEventType() == EventType.STOP_ALL) {
				State next = state.stopTimer(this, timestamp);
				updateState(next, timestamp);
			}
		}
				
	}
	
	private static class InvalidFileException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		
		public InvalidFileException(String fileType) {
			super("expected ACTIVITY file type, was " + fileType);
		}
		
	}
	
	private static interface State {
		
		void enter(StateContext context, long timestamp);
		
		State startTimer(StateContext context, Long timestamp);
		
		State record(StateContext context, RecordMesg mesg);
		
		State lapEnd(StateContext context, LapMesg mesg);
		
		State stopTimer(StateContext context, Long timestamp);
		
		void leave(StateContext context, long timestamp);
		
	}
	
	private static interface StateContext {
		
		void addTrackpoint(Trackpoint trackpoint);

		void startTrack(long timestamp);

		void endTrack(long timestamp);
		
	}
	
	private static class RunningState implements State {

		private long trackStart;
		
		@Override
		public void enter(StateContext context, long timestamp) {
			context.startTrack(timestamp);
			this.trackStart = timestamp;
		}
		
		@Override
		public void leave(StateContext context, long timestamp) {
			context.endTrack(timestamp);
		}
		
		@Override
		public State startTimer(StateContext context, Long timestamp) {
			throw new IllegalStateException("timer already running");
		}

		@Override
		public State record(StateContext context, RecordMesg mesg) {
			long timestamp = mesg.getTimestamp().getDate().getTime();
			
			Trackpoint trackpoint = new Trackpoint(timestamp - trackStart);
			
			if (mesg.getPositionLat() != null && mesg.getPositionLong() != null) {
				trackpoint.addAttribute(new PositionAttribute(semitodeg(mesg.getPositionLat()), semitodeg(mesg.getPositionLong())));
			}
			
			if (mesg.getAltitude() != null) {
				trackpoint.addAttribute(new AltitudeAttribute(mesg.getAltitude().doubleValue()));
			}
			
			if (mesg.getDistance() != null) {
				trackpoint.addAttribute(new DistanceAttribute(mesg.getDistance().doubleValue()));
			}

			if (mesg.getHeartRate() != null) {
				trackpoint.addAttribute(new HeartRateAttribute(mesg.getHeartRate().intValue()));
			}
			
			if (mesg.getSpeed() != null) {
				trackpoint.addAttribute(new SpeedAttribute(mesg.getSpeed().doubleValue()));
			}
			
			if (mesg.getCadence() != null) {
				trackpoint.addAttribute(new CadenceAttribute(mesg.getCadence().intValue()));
			}
			
			if (mesg.getAccumulatedPower() != null) {
				trackpoint.addAttribute(new PowerAttribute(mesg.getAccumulatedPower().doubleValue()));
			}
			
//			System.out.println("  temperature = " + mesg.getTemperature());
//			System.out.println("  cycles      = " + mesg.getCycles());
			
			context.addTrackpoint(trackpoint);
		
			return this;
		}

		@Override
		public State lapEnd(StateContext context, LapMesg mesg) {
			return this;
		}

		@Override
		public State stopTimer(StateContext context, Long timestamp) {
			return new StoppedState();
		}
		
	}
	
	private static class StoppedState implements State {

		@Override
		public void enter(StateContext context, long timestamp) {
			// ignored
		}
		
		@Override
		public void leave(StateContext context, long timestamp) {
			// ignored
		}
		
		@Override
		public State startTimer(StateContext context, Long timestamp) {
			return new RunningState();
		}

		@Override
		public State record(StateContext context, RecordMesg mesg) {
			throw new IllegalStateException("timer stopped");
		}

		@Override
		public State lapEnd(StateContext context, LapMesg mesg) {
			// ignored
			return this;
		}

		@Override
		public State stopTimer(StateContext context, Long timestamp) {
			throw new IllegalStateException("timer already stopped");
		}
		
	}

}
