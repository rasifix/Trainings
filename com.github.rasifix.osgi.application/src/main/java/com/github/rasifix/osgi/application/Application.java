package com.github.rasifix.osgi.application;

/**
 * Interface to be implemented by applications that are launched by 
 * com.github.rasifix.osgi.Launcher.
 */
public interface Application {
	
	/**
	 * Start method taking the arguments from the command line.
	 * 
	 * @param args the arguments from the command line
	 */
	void start(String[] args);
	
}
