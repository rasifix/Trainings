package com.github.rasifix.trainings.ui.client;

import com.github.rasifix.trainings.ui.client.activities.ActivitiesPlace;
import com.github.rasifix.trainings.ui.client.equipments.EquipmentsPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ ActivitiesPlace.Tokenizer.class, EquipmentsPlace.Tokenizer.class })
public interface TrainingsPlaceHistoryMapper extends PlaceHistoryMapper {

}
