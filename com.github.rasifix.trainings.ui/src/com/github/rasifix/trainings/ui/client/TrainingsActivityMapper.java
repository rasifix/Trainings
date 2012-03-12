package com.github.rasifix.trainings.ui.client;

import javax.inject.Inject;

import com.github.rasifix.trainings.ui.client.activities.ActivitiesActivity;
import com.github.rasifix.trainings.ui.client.activities.ActivitiesPlace;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsActivity;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsPlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Provider;

public class TrainingsActivityMapper implements ActivityMapper {

	private Provider<ActivitiesActivity> activitiesActivity;
	
	private Provider<EquipmentsActivity> equipmentsActivity;

	@Inject
	public void setActivitiesActivity(Provider<ActivitiesActivity> activitiesActivity) {
		this.activitiesActivity = activitiesActivity;
	}
	
	@Inject
	public void setEquipmentsActivity(Provider<EquipmentsActivity> equipmentsActivity) {
		this.equipmentsActivity = equipmentsActivity;
	}
	
	@Override
	public Activity getActivity(Place place) {
		if (place instanceof ActivitiesPlace) {
			ActivitiesPlace activitiesPlace = (ActivitiesPlace) place;
			ActivitiesActivity provided = activitiesActivity.get();
			provided.setRange(activitiesPlace.getYear(), activitiesPlace.getMonth());
			return provided;
		} else if (place instanceof EquipmentsPlace) {
			return equipmentsActivity.get();
		}
		return null;
	}

}
