package com.github.rasifix.trainings.shell;


public interface CommandContext {
	
	String getArgument(int idx);
	
	String[] getArguments();
	
	Object getCurrent();
	
	boolean containsKey(Object key);
	
	void put(Object key, Object value);
	
	Object get(Object key);
	
}
