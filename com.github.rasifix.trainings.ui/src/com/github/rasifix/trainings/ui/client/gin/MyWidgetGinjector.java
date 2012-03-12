package com.github.rasifix.trainings.ui.client.gin;

import com.github.rasifix.trainings.ui.client.activities.ActivitiesView;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

@GinModules(MyWidgetClientModule.class)
public interface MyWidgetGinjector extends Ginjector {
	
	ActivitiesView getActivitiesView();

	EventBus getEventBus();

	PlaceController getPlaceController();

	ActivityMapper getActivityMapper();

	PlaceHistoryMapper getPlaceHistoryMapper();
  
}