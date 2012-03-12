package com.github.rasifix.trainings.ui.client.gin;

import com.github.rasifix.trainings.ui.client.TrainingsActivityMapper;
import com.github.rasifix.trainings.ui.client.TrainingsPlaceHistoryMapper;
import com.github.rasifix.trainings.ui.client.activities.ActivitiesActivity;
import com.github.rasifix.trainings.ui.client.activities.ActivitiesView;
import com.github.rasifix.trainings.ui.client.activities.ActivitiesViewImpl;
import com.github.rasifix.trainings.ui.client.activities.ActivityRepository;
import com.github.rasifix.trainings.ui.client.activities.ActivityRepositoryImpl;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentRepository;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentRepositoryImpl;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsActivity;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsView;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsViewImpl;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

public class MyWidgetClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		// views
		bind(ActivitiesView.class).to(ActivitiesViewImpl.class);
		bind(EquipmentsView.class).to(EquipmentsViewImpl.class);
		
		// services
		bind(ActivityRepository.class).to(ActivityRepositoryImpl.class).in(Singleton.class);
		bind(EquipmentRepository.class).to(EquipmentRepositoryImpl.class).in(Singleton.class);
		
		// presenters
		bind(ActivitiesActivity.class);
		bind(EquipmentsActivity.class).in(Singleton.class);
		
		bind(ActivityMapper.class).to(TrainingsActivityMapper.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(TrainingsPlaceHistoryMapper.class).in(Singleton.class);
		
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).to(InjectablePlaceController.class).in(Singleton.class);
	}
	
}
