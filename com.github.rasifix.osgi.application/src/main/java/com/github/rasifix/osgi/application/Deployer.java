package com.github.rasifix.osgi.application;

import java.io.File;
import java.io.IOException;

public interface Deployer {

	boolean canDeploy(File file);
	
	void deploy(File file) throws IOException;
	
}
