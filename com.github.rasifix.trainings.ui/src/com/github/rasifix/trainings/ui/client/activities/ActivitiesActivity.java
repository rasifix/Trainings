package com.github.rasifix.trainings.ui.client.activities;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.github.rasifix.trainings.ui.client.activities.ActivityRepository.Callback;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class ActivitiesActivity extends AbstractActivity implements ActivitiesPresenter {
	
	private final ActivitiesView view;
	private final ActivityRepository repository;
	private final PlaceController controller;
	private int year;
	private int month;
	
	@Inject
	public ActivitiesActivity(ActivitiesView view, ActivityRepository repository, PlaceController controller) {
		this.view = view;
		this.repository = repository;
		this.controller = controller;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view.asWidget());
		repository.findActivities(month, year, new Callback<ViewResult<ActivityOverview>>() {
			@Override
			public void onSuccess(ViewResult<ActivityOverview> result) {
				view.setActivities(result);
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				GWT.log("failed to load activities", exception);
			}
		});
	}
	
	@Override
	public void next() {
		int nextMonth = month(month + 1);
		int nextYear = month == 12 ? year + 1 : year;
		controller.goTo(new ActivitiesPlace(nextMonth, nextYear));
		System.out.println("go to " + nextYear + "-" + nextMonth);
	}
	
	public void previous() {
		int previousMonth = month(month - 1);
		int previousYear = month == 1 ? year - 1 : year;
		controller.goTo(new ActivitiesPlace(previousMonth, previousYear));
	}

	private int month(int month) {
		if (month == 13) {
			return 1;
		} else if (month == 0) {
			return 12;
		} else {
			return month;
		}
	}

	public void setRange(int year, int month) {
		this.year = year;
		this.month = month;
	}

}
