package com.github.rasifix.trainings.shell.internal.commands;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

import jline.Completor;

@Component
public class FrechetDistanceCommand implements Command {

	private static final String NAME = "frechet";
	
	private ActivityRepository repository;
	
	@Reference
	public void setRepository(ActivityRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <activity-id> <activity-id>";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity a1 = repository.getActivity(context.getArgument(0));
		Activity a2 = repository.getActivity(context.getArgument(1));
		
		System.out.println(frechet(a1, a2));
		
		return context.getCurrent();
	}

	private double frechet(Activity a1, Activity a2) {
		final Position[] p = positions(a1);
		final Position[] q = positions(a2);
		
		final double[][] ca = new double[p.length][];
		for (int i = 0; i < p.length; i++) {
			ca[i] = new double[q.length];
			for (int j = 0; j < q.length; j++) {
				ca[i][j] = -1;
			}
		}
		
		class C_ulator {
			
			public double c(int i, int j) {
				if (ca[i][j] > -1) return ca[i][j];
				if (i == 0 && j == 0) {
					ca[i][j] = distance(p[0], q[0]);
				} else if (i > 0 && j == 0) {
					ca[i][j] = Math.max(c(i - 1, 0), distance(p[i], q[0]));
				} else if (i == 0 && j > 0) {
					ca[i][j] = Math.max(c(0, j - 1), distance(p[0], q[j]));
				} else if (i > 0 && j > 0) {
					ca[i][j] = Math.max(Math.min(c(i - 1, j), Math.min(c(i - 1, j - 1), c(i, j - 1))), distance(p[i], q[j]));
				} else {
					ca[i][j] = Double.POSITIVE_INFINITY;
				}
				return ca[i][j];
			}

			private double distance(Position p1, Position p2) {
				final double R = 6371000; // km
				double dLat = toRad(p2.getLatitude() - p1.getLatitude());
				double dLon = toRad(p2.getLongitude() - p1.getLongitude());
				double lat1 = toRad(p1.getLatitude());
				double lat2 = toRad(p2.getLatitude());

				double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2); 
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); 
				return R * c;
			}

		}

		return new C_ulator().c(p.length - 1, q.length - 1);
	}
	
	private Position[] positions(Activity activity) {
		List<Position> result = new ArrayList<Position>();
		for (Track track : activity.getTracks()) {
			for (Trackpoint trackpoint : track.getTrackpoints().select(PositionAttribute.class)) {
				result.add(trackpoint.getPosition());
			}
		}
		return result.toArray(new Position[result.size()]);
	}

	private double toRad(double angle) {
		return angle / 180 * Math.PI;
	}

}
