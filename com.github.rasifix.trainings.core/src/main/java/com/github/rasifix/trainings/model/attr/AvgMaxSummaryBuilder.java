package com.github.rasifix.trainings.model.attr;

import com.github.rasifix.trainings.model.AttributeSummaryBuilder;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.TrackpointAttribute;
import com.github.rasifix.trainings.model.TrackpointSequence;

public final class AvgMaxSummaryBuilder implements AttributeSummaryBuilder<AvgMaxSummary> {
	
	private final Class<? extends TrackpointAttribute> attributeType;

	public AvgMaxSummaryBuilder(Class<? extends TrackpointAttribute> attributeType) {
		this.attributeType = attributeType;
	}

	@Override
	public Class<? extends TrackpointAttribute> getAttributeType() {
		return attributeType;
	}

	@Override
	public AvgMaxSummary buildSummary(TrackpointSequence trackpoints) {
		if (trackpoints.isEmpty()) {
			return null;
		}
		
		int max = Integer.MIN_VALUE;
		
		long sum = 0;
		int time = 0;
		
		for (Trackpoint trackpoint : trackpoints) {
			if (trackpoints.isLast(trackpoint)) {
				break;
			}
			
			TrackpointAttribute current = trackpoint.getAttribute(getAttributeType());
			int currentValue = (Integer) current.getValue();
			max = Math.max(max, currentValue);
			
			TrackpointAttribute next = trackpoint.getAttribute(getAttributeType());
			int nextValue = (Integer) next.getValue();
			
			int intervalAvg = currentValue + Math.round((nextValue - currentValue) / 2f);
			int elapsed = (int) (trackpoint.getElapsedTime() - trackpoints.next(trackpoint).getElapsedTime());
			
			time += elapsed;
			sum += elapsed * intervalAvg;
		}

		int avg = (int) Math.round(1f * sum / time);
		return new AvgMaxSummary(time, avg, max);
	}
}