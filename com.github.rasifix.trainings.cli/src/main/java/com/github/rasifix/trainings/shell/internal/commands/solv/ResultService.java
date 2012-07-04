package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.io.IOException;
import java.util.List;

public interface ResultService {
	
	List<Event> listEvents(int year) throws IOException;
	
	List<Category> listCategories(Event event) throws IOException;
	
	List<Runner> listRunners(Category category) throws IOException;
	
}
