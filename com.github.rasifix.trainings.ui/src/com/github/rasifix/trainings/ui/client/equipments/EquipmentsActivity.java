package com.github.rasifix.trainings.ui.client.equipments;

import javax.inject.Inject;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentRepository.Callback;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class EquipmentsActivity extends AbstractActivity implements EquipmentsPresenter {

	private final EquipmentsView view;
	
	private final EquipmentRepository repository;
	
	@Inject
	public EquipmentsActivity(EquipmentsView view, EquipmentRepository repository) {
		this.view = view;
		this.repository = repository;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view.asWidget());
		repository.findEquipments(new Callback<ViewResult<Equipment>>() {
			@Override
			public void onSuccess(ViewResult<Equipment> result) {
				view.setEquipments(result);
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				GWT.log("failed to load activities", exception);
			}
		});

	}

}
