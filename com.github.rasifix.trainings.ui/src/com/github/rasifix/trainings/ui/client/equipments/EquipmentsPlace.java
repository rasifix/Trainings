package com.github.rasifix.trainings.ui.client.equipments;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class EquipmentsPlace extends Place {
	
	public EquipmentsPlace() { }
	
    public static class Tokenizer implements PlaceTokenizer<EquipmentsPlace> {
        @Override
        public String getToken(EquipmentsPlace place) {
            return "";
        }

		@Override
        public EquipmentsPlace getPlace(String token) {
            return new EquipmentsPlace();
        }
    }
	
}
