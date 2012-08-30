package com.github.rasifix.trainings.orienteering;

import java.io.IOException;

public interface EventRepository {

	EventKey addEvent(Event event) throws IOException;
	
}
