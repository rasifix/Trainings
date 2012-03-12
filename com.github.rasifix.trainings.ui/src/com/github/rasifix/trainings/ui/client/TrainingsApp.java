package com.github.rasifix.trainings.ui.client;

import com.github.rasifix.trainings.ui.client.activities.ActivitiesPlace;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsPlace;
import com.github.rasifix.trainings.ui.client.gin.MyWidgetGinjector;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TrainingsApp implements EntryPoint {

	private final MyWidgetGinjector injector = GWT.create(MyWidgetGinjector.class);

	interface MyUiBinder extends UiBinder<SimplePanel, TrainingsApp> {}
	
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	SimplePanel display;
	
	@UiField
	Anchor activities;
	
	@UiField
	Anchor equipments;
	
	@UiHandler("activities")
	public void selectActivities(ClickEvent e) {
		e.preventDefault();
		injector.getPlaceController().goTo(new ActivitiesPlace(3, 2012));
		equipments.getElement().getParentElement().removeClassName("active");
		activities.getElement().getParentElement().addClassName("active");
	}
	
	@UiHandler("equipments")
	public void selectEquipments(ClickEvent e) {
		e.preventDefault();
		injector.getPlaceController().goTo(new EquipmentsPlace());
		activities.getElement().getParentElement().removeClassName("active");
		equipments.getElement().getParentElement().addClassName("active");
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		SimplePanel root = uiBinder.createAndBindUi(this);
		
		EventBus eventBus = injector.getEventBus();
		PlaceController placeController = injector.getPlaceController();

		ActivityMapper activityMapper = injector.getActivityMapper();
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(display);

		PlaceHistoryMapper historyMapper = injector.getPlaceHistoryMapper();
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, new ActivitiesPlace(2, 2012));
		
		RootPanel.get().add(root);
		
		historyHandler.handleCurrentHistory();
	}
}
