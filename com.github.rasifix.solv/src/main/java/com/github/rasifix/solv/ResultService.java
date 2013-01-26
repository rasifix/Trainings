package com.github.rasifix.solv;

import java.io.IOException;

public interface ResultService {

	Event getEvent(int year, String title) throws IOException;
	
}
