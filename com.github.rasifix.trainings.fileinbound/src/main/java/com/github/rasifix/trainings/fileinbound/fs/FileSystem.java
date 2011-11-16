package com.github.rasifix.trainings.fileinbound.fs;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface FileSystem {
	
	FileHandle getFile(String path);

	FileHandle getFile(FileHandle directory, String file);

	Reader getReader(FileHandle file) throws IOException;

	List<FileHandle> listFiles(FileHandle directory, FileFilter filter);

	Writer getWriter(FileHandle file) throws IOException;
	
}
