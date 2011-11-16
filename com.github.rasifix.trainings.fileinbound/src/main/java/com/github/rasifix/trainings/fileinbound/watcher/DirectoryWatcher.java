package com.github.rasifix.trainings.fileinbound.watcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.github.rasifix.trainings.fileinbound.fs.FileFilter;
import com.github.rasifix.trainings.fileinbound.fs.FileHandle;
import com.github.rasifix.trainings.fileinbound.fs.FileSystem;
import com.github.rasifix.trainings.fileinbound.fs.impl.DefaultFileSystem;
import com.github.rasifix.trainings.fileinbound.watcher.impl.DefaultWatchCache;


public class DirectoryWatcher implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(DirectoryWatcher.class.getName());
	
	private final Map<FileHandle, Long> added = new HashMap<FileHandle, Long>();
	
	private final FileSystem fileSystem;
	
	private final WatchCache cache;

	private final DirectoryWatcherDelegate delegate;
	
	private final FileHandle directory;
	
	private FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(final FileHandle file) {
			return file.isFile();
		}
	};
	
	public DirectoryWatcher(final FileSystem fileSystem, final String path, final WatchCache cache, final DirectoryWatcherDelegate delegate) throws IOException {
		this.fileSystem = fileSystem;
		this.delegate = delegate;
		this.cache = cache;
		
		final FileHandle directory = fileSystem.getFile(path);
		if (!directory.exists()) {
			throw new IllegalArgumentException("directory does not exist: " + path);
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("given directory is not a directory: " + path);
		}
		this.directory = directory;
	}

	private void flushIndex() throws IOException {
		cache.flush();
	}
	
	private Long lastSeen(FileHandle file) {
		return cache.lastSeen(file);
	}
	
	private void markLastSeen(FileHandle file) {
		cache.markLastSeen(file);
	}
	
	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public void run() {
		LOGGER.fine("scanning directory for changes");
		
		boolean changed = false;
		
		final List<FileHandle> files = fileSystem.listFiles(directory, filter);		
		for (final FileHandle file : files) {
			if (file.getName().startsWith(".")) {
				continue;
			}
			
			final Long lastModified = lastSeen(file);
			if (lastModified == null) {
				final Long lastSize = added.get(file);
				if (lastSize == null) {
					LOGGER.fine("detected new file " + file.getName());
					added.put(file, file.size());
					
				} else if (lastSize == file.size()) {
					LOGGER.fine("new file has been detected in a stable state: " + file.getName());
					added.remove(file);
					delegate.fileAdded(file);
					markLastSeen(file);
					changed = true;
					
				} else {
					LOGGER.fine("detected file has grown: " + file.getName());
					added.put(file, file.size());
				}
				
			} else if (lastModified < file.lastModified()) {
				LOGGER.fine("file has been modified: " + file.getName());
				delegate.fileModified(file);
				markLastSeen(file);
				changed = true;
			}
		}
		
		try {
			if (changed) {
				flushIndex();
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot flush index file", e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		FileSystem fileSystem = new DefaultFileSystem();
		WatchCache watchCache = new DefaultWatchCache(fileSystem, "/Users/sir/Downloads");
		DirectoryWatcher watcher = new DirectoryWatcher(fileSystem, "/Users/sir/Downloads", watchCache, new DirectoryWatcherDelegate() {
			@Override
			public void fileAdded(FileHandle fileHandle) {
				System.out.println("A " + fileHandle.getName());
			}

			@Override
			public void fileModified(FileHandle fileHandle) {
				System.out.println("M " + fileHandle.getName());				
			}
		});
		service.scheduleWithFixedDelay(watcher, 0, 30, TimeUnit.SECONDS);
	}
	
}
