package com.github.rasifix.trainings.orienteering.internal;

import org.junit.Test;

import com.github.rasifix.trainings.orienteering.Event;

public class ResultServiceImplTest {
	
	@Test
	public void testSample() throws Exception {
		ResultServiceImpl service = new ResultServiceImpl();
		service.setServiceUrl("http://www.o-l.ch/cgi-bin/results");
		
		Event event = service.getEvent(2012, "Schweiz. Meisterschaft im Langdistanz-OL");
		
	}
	
}
