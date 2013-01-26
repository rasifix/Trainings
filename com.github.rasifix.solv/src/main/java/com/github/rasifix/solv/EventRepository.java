package com.github.rasifix.solv;

import java.io.IOException;

public interface EventRepository {

	EventKey addEvent(Event event) throws IOException;
	
}
