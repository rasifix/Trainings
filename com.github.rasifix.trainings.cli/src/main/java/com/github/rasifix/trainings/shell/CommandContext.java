package com.github.rasifix.trainings.shell;

import java.io.File;
import java.io.IOException;
import java.util.List;


public interface CommandContext {
	
	String getArgument(int idx);
	
	String[] getArguments();
	
	Object getCurrent();

	void setCurrent(Object current);
	
	boolean containsKey(Object key);
	
	void put(Object key, Object value);
	
	Object get(Object key);

	List<File> resolveFiles(String fileName);

	void execute(String command) throws IOException;
	
}
