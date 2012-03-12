package com.github.rasifix.trainings.ui.client.gin;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class InjectablePlaceController extends PlaceController {

	@Inject
	public InjectablePlaceController(EventBus eventBus) {
		super(eventBus);
	}
	
	@Override
	public void goTo(Place newPlace) {
		super.goTo(newPlace);
	}
	
}
