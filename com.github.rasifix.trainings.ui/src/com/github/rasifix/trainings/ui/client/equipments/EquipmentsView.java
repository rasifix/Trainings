package com.github.rasifix.trainings.ui.client.equipments;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.user.client.ui.IsWidget;

public interface EquipmentsView extends IsWidget {
	
	void setPresenter(EquipmentsPresenter presenter);

	void setEquipments(ViewResult<Equipment> result);
	
}
