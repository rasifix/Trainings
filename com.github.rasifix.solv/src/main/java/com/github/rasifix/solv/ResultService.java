package com.github.rasifix.solv;

import java.io.IOException;
import java.util.List;

public interface ResultService {

	List<EventOverview> listEvents(int year) throws IOException;
	
	Event queryEvent(int year, String title) throws IOException;
	
}
