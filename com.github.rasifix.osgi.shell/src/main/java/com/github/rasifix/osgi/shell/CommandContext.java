package com.github.rasifix.osgi.shell;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
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
	
	void push(Object o);
	
	Object peek();
	
	Object pop();
	
	Iterator<Object> stackIterator();

	void execute(String command) throws IOException;
	
}
