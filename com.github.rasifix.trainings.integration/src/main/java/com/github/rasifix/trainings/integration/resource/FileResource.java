package com.github.rasifix.trainings.integration.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResource implements Resource {

	private final File target;
	
	public FileResource(File target) {
		this.target = target;
	}
	
	@Override
	public String getName() {
		return target.getName();
	}
	
	public File getFile() {
		return target;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(target);
	}

}
