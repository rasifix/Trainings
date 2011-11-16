package com.github.rasifix.trainings.fileinbound.fs;

public interface FileHandle {
	
	boolean exists();
	
	boolean isDirectory();
	
	boolean isFile();

	long lastModified();
	
	String getName();
	
	String getFullPath();

	long size();
	
}
