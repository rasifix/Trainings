package com.github.rasifix.solv.service.internal;

import org.junit.Test;

public class ResultServiceImplTest {
	
	@Test
	public void testSample() throws Exception {
		ResultServiceImpl service = new ResultServiceImpl();
		service.setServiceUrl("http://www.o-l.ch/cgi-bin/results");
		
		service.queryEvent(2012, "Schweiz. Meisterschaft im Langdistanz-OL");
	}
	
}
