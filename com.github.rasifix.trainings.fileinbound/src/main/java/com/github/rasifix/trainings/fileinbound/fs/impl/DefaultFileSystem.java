package com.github.rasifix.trainings.fileinbound.fs.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.fileinbound.fs.FileFilter;
import com.github.rasifix.trainings.fileinbound.fs.FileHandle;
import com.github.rasifix.trainings.fileinbound.fs.FileSystem;


public class DefaultFileSystem implements FileSystem {

	@Override
	public FileHandle getFile(String path) {
		return new DefaultFileHandle(new File(path));
	}

	@Override
	public FileHandle getFile(FileHandle directory, String file) {
		final DefaultFileHandle dir = (DefaultFileHandle) directory;
		return new DefaultFileHandle(new File(dir.getFile(), file));
	}

	@Override
	public Reader getReader(FileHandle file) throws IOException {
		final DefaultFileHandle f = (DefaultFileHandle) file;
		return new InputStreamReader(new FileInputStream(f.getFile()));
	}
	
	@Override
	public Writer getWriter(FileHandle file) throws IOException {
		final DefaultFileHandle f = (DefaultFileHandle) file;
		return new OutputStreamWriter(new FileOutputStream(f.getFile()));
	}

	@Override
	public List<FileHandle> listFiles(final FileHandle directory, final FileFilter filter) {
		final DefaultFileHandle handle = (DefaultFileHandle) directory;
		final List<FileHandle> result = new LinkedList<FileHandle>();
		
		final File[] files;
		if (filter == null) {
			files = handle.getFile().listFiles();
		} else {
			files = handle.getFile().listFiles(new java.io.FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return filter.accept(createHandle(pathname));
				}
			});
		}
		
		if (files != null) {
			for (final File file : files) {
				result.add(createHandle(file));
			}
		}
		
		return result;
	}

	private static DefaultFileHandle createHandle(final File file) {
		return new DefaultFileHandle(file);
	}
	
	private static class DefaultFileHandle implements FileHandle {

		private final File file;
		
		public DefaultFileHandle(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}
		
		@Override
		public boolean exists() {
			return file.exists();
		}

		@Override
		public boolean isDirectory() {
			return file.isDirectory();
		}

		@Override
		public boolean isFile() {
			return file.isFile();
		}

		@Override
		public long lastModified() {
			return file.lastModified();
		}

		@Override
		public String getName() {
			return file.getName();
		}
		
		@Override
		public String getFullPath() {
			return file.getAbsolutePath();
		}
		
		@Override
		public long size() {
			return file.length();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null) {
				return false;
			} else if (getClass() == obj.getClass()) {
				DefaultFileHandle other = (DefaultFileHandle) obj;
				return other.file.equals(this.file);
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return file.hashCode();
		}
		
	}

}
