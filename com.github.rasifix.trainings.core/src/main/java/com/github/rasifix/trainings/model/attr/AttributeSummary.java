package com.github.rasifix.trainings.model.attr;

public interface AttributeSummary<T extends AttributeSummary<T>> {

	T merge(T summary);
	
}
