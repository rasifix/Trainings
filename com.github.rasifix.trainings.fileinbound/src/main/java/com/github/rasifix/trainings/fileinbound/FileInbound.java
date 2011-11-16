package com.github.rasifix.trainings.fileinbound;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;

import com.github.rasifix.trainings.fileinbound.fs.FileHandle;
import com.github.rasifix.trainings.fileinbound.fs.FileSystem;
import com.github.rasifix.trainings.fileinbound.fs.impl.DefaultFileSystem;
import com.github.rasifix.trainings.fileinbound.watcher.DirectoryWatcher;
import com.github.rasifix.trainings.fileinbound.watcher.DirectoryWatcherDelegate;
import com.github.rasifix.trainings.fileinbound.watcher.WatchCache;
import com.github.rasifix.trainings.fileinbound.watcher.impl.DefaultWatchCache;
import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageChannel;
import com.github.rasifix.trainings.integration.message.MessageBuilder;
import com.github.rasifix.trainings.integration.resource.FileResource;

public class FileInbound {
	
	private static final String DIRECTORY_KEY = "directory";
	
	private MessageChannel inbound;

	private DirectoryWatcher watcher;

	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> scheduledTask;
	
	public void setInbound(MessageChannel inbound) {
		this.inbound = inbound;
	}
	
	public void activate(ComponentContext context) throws Exception {
		String directory = getDirectory(context);
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		scheduler = Executors.newScheduledThreadPool(1);
		FileSystem fileSystem = new DefaultFileSystem();
		WatchCache watchCache = new DefaultWatchCache(fileSystem, directory);
		watcher = new DirectoryWatcher(fileSystem, directory, watchCache, new DirectoryWatcherDelegate() {
			@Override
			public void fileAdded(FileHandle fileHandle) {
				try {
					File file = new File(fileHandle.getFullPath());
					Message message = MessageBuilder.withPayload(new FileResource(file))
								                    .setHeader("file", file)
								                    .build();
					inbound.send(message);
					
				} catch (Throwable th) {
					th.printStackTrace(System.out);
					throw new RuntimeException("failed to process " + fileHandle.getFullPath(), th);
				}
			}

			@Override
			public void fileModified(FileHandle fileHandle) {
				// ignore modifications				
			}
		});
		scheduledTask = scheduler.scheduleWithFixedDelay(watcher, 0, 10, TimeUnit.SECONDS);
	}
	
	public void deactivate() throws Exception {
		if (scheduler != null && scheduledTask != null) {
			scheduledTask.cancel(false);
			scheduler.shutdown();
		}
	}

	private String getDirectory(ComponentContext context) {
		String directory = (String) context.getProperties().get(DIRECTORY_KEY);
		if (directory.startsWith("~")) {
			directory = System.getProperty("user.home") + directory.substring(1);
		}
		return directory;
	}
	
}
