package com.github.rasifix.trainings.ui.client.activities;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.user.client.ui.IsWidget;

public interface ActivitiesView extends IsWidget {

	void setPresenter(ActivitiesPresenter presenter);
	
	void setActivities(ViewResult<ActivityOverview> result);
	
}
