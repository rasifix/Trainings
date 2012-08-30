package com.github.rasifix.trainings.orienteering;

import java.io.IOException;
import java.io.OutputStream;

public interface EventWriter {
	
	void writeEvent(Event event, OutputStream stream) throws IOException;
	
}
