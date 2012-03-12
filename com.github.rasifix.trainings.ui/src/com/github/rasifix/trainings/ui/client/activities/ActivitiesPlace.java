package com.github.rasifix.trainings.ui.client.activities;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ActivitiesPlace extends Place {
	
	private final int month;
	private final int year;

	public ActivitiesPlace(int month, int year) {
		this.month = month;
		this.year = year;
	}
	
	public ActivitiesPlace(String token) {
		this(getMonth(token), getYear(token));
	}

	private static int getMonth(String token) {
		return Integer.parseInt(token.split("[-]")[1]);
	}

	private static int getYear(String token) {
		return Integer.parseInt(token.split("[-]")[0]);
	}

	public int getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}
	
    public static class Tokenizer implements PlaceTokenizer<ActivitiesPlace> {
        @Override
        public String getToken(ActivitiesPlace place) {
            return place.getYear() + "-" + pad(place.getMonth());
        }

        private String pad(int month) {
			return month < 10 ? "0" + month : "" + month;
		}

		@Override
        public ActivitiesPlace getPlace(String token) {
            return new ActivitiesPlace(token);
        }
    }
	
}
