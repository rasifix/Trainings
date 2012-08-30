package com.github.rasifix.trainings.orienteering;

import java.io.IOException;

public interface ResultService {

	Event getEvent(int year, String title) throws IOException;
	
}
