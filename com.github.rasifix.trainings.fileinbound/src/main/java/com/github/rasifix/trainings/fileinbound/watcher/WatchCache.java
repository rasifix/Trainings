package com.github.rasifix.trainings.fileinbound.watcher;

import java.io.IOException;

import com.github.rasifix.trainings.fileinbound.fs.FileHandle;


public interface WatchCache {
	
	Long lastSeen(FileHandle file);
	
	void markLastSeen(FileHandle file);
	
	void flush() throws IOException;
	
}
