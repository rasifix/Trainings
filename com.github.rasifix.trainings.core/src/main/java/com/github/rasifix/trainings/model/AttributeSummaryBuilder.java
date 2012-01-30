package com.github.rasifix.trainings.model;

import com.github.rasifix.trainings.model.attr.AttributeSummary;

public interface AttributeSummaryBuilder<T extends AttributeSummary<T>> {
	
	T buildSummary(TrackpointSequence trackpoints);

	Class<? extends TrackpointAttribute> getAttributeType();
	
}
