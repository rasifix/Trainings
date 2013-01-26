package com.github.rasifix.osgi.shell.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileNameResolver {
	
	private final File wdir;

	public FileNameResolver(File wdir) {
		this.wdir = wdir;
	}
	
	public List<File> resolveFiles(String argument) {
		System.out.println("resolve " + argument);
		
		List<File> result = new LinkedList<File>();
		if (argument.startsWith("~")) {
			argument = System.getProperty("user.home") + argument.substring(1);
		}
		
		if (argument.contains("*")) {
			int index = argument.indexOf('*');
			String folder = argument.substring(0, index);
			File dir = null;
			if (!folder.startsWith("/")) {
				dir = new File(wdir, folder);
			} else {
				dir = new File(folder);
			}
			
			File[] listedFiles = dir.listFiles(new ExtensionFilter(argument.substring(index + 1)));
			if (listedFiles != null) {
				result.addAll(Arrays.asList(listedFiles));
			}
		} else if (argument.startsWith("/")) {
			result.add(new File(argument));
		} else {
			result.add(new File(wdir, argument));
		}
		return result;

	}
	
	private static class ExtensionFilter implements FilenameFilter {

		private final String extension;

		public ExtensionFilter(String extension) {
			this.extension = extension.substring(2);
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(extension);
		}
		
	}

}
