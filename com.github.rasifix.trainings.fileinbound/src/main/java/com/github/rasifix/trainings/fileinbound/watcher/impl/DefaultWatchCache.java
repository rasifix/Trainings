package com.github.rasifix.trainings.fileinbound.watcher.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.github.rasifix.trainings.fileinbound.fs.FileHandle;
import com.github.rasifix.trainings.fileinbound.fs.FileSystem;
import com.github.rasifix.trainings.fileinbound.watcher.WatchCache;


public class DefaultWatchCache implements WatchCache {

	public static final String CACHE_FILE = ".watchcache";
	
	private final Map<FileHandle, Long> cache = new HashMap<FileHandle, Long>();
	
	private final FileSystem fileSystem;
	
	private final FileHandle directory;

	private final FileHandle cacheFile;
	
	public DefaultWatchCache(final FileSystem fileSystem, String path) throws IOException {
		this.directory = fileSystem.getFile(path);
		this.fileSystem = fileSystem;
		this.cacheFile = fileSystem.getFile(directory, CACHE_FILE);
		this.populateIndex();
	}
	
	private void populateIndex() throws IOException {
		if (cacheFile.exists()) {
			Reader reader = fileSystem.getReader(cacheFile);
			Properties p = new Properties();
			p.load(reader);
			for (final Entry<Object, Object> entry : p.entrySet()) {
				final FileHandle fileHandle = fileSystem.getFile(directory, (String) entry.getKey());
				cache.put(fileHandle, new Long((String) entry.getValue()));
			}
		}
	}

	@Override
	public Long lastSeen(FileHandle file) {
		return cache.get(file);
	}

	@Override
	public void markLastSeen(FileHandle file) {
		cache.put(file, file.lastModified());
	}

	@Override
	public void flush() throws IOException {
		final PrintWriter writer = new PrintWriter(fileSystem.getWriter(cacheFile));
		
		for (final Entry<FileHandle, Long> entry : cache.entrySet()) {
			writer.write(entry.getKey().getName());
			writer.write("=");
			writer.write(entry.getValue().toString());
			writer.write("\n");
		}
		
		writer.close();
	}

}
