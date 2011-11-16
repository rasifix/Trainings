package com.github.rasifix.trainings.fileinbound.watcher;

import com.github.rasifix.trainings.fileinbound.fs.FileHandle;

public interface DirectoryWatcherDelegate {
	
	void fileAdded(FileHandle fileHandle);
	
	void fileModified(FileHandle fileHandle);
	
}
