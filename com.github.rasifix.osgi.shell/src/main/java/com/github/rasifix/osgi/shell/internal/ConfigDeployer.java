package com.github.rasifix.osgi.shell.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.application.Deployer;

@Component
public class ConfigDeployer implements Deployer {
	
	private ConfigurationAdmin configAdmin;

	@Reference
	public void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	@Override
	public boolean canDeploy(File file) {
		return file.getName().endsWith(".cfg");
	}
	
	@Override
	public void deploy(File file) throws IOException {
		System.out.println("... deploying config from " + file.getName());
		String pid = file.getName().substring(0, file.getName().length() - ".cfg".length());
		Properties properties = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
			Configuration configuration = configAdmin.getConfiguration(pid, null);
			Dictionary<String, Object> dict = configuration.getProperties();
			if (dict == null) {
				dict = new Hashtable<>();
			}
			merge(properties, dict);
			configuration.update(dict);
			
			System.out.println("... updated config with pid = " + pid);
		}
	}

	private void merge(Properties properties, Dictionary<String, Object> dict) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			dict.put((String) entry.getKey(), entry.getValue());
		}
	}

}
