package com.github.rasifix.osgi.shell.internal.commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

import jline.Completor;
import jline.NullCompletor;

@Component
public class OsgiConfigCommand implements Command {

	private static final String NAME = "osgi:config";
	private ConfigurationAdmin configAdmin;
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUsage() {
		return NAME;
	}
	
	@Reference
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		String[] args = context.getArguments();
		if (args.length == 0) {
			reloadConfig();
		} else if (args.length == 1) {
			showConfig(args[0]);
		} else if (args.length == 3 && "delete".equals(args[0])) {
			configAdmin.getConfiguration(args[1], null).delete();
		} else if (args.length == 3) {
			updateConfig(args[0], args[1], args[2]);
		}
				
		return context.getCurrent();
	}

	private void reloadConfig() throws IOException {
        for (File file : new File("load").listFiles()) {
        	String pid = file.getName().substring(0, file.getName().length() - 4);
        	System.out.println("updating config for " + pid);
        	Configuration config = configAdmin.getConfiguration(pid, null);
        	Properties props = new Properties();
        	
        	try (FileReader reader = new FileReader(file)) {
        		props.load(reader);
        	}
        	
        	Hashtable<String, Object> ht = new Hashtable<>();
        	props.forEach((key, value) -> ht.put((String) key, value));
        	
        	config.update(ht);
        }
	}

	private void showConfig(String pid) throws IOException, InvalidSyntaxException {
		if ("all".equals(pid)) {
			Configuration[] configurations = configAdmin.listConfigurations(null);
			if (configurations == null) {
				System.out.println("no configuration found!");
				return;
			}
			
			for (Configuration configuration : configurations) {
				System.out.println(configuration.getPid());
			}
			return;
		}
		
		Configuration configuration = configAdmin.getConfiguration(pid, null);
		System.out.println("configuration for pid " + pid);
		Dictionary<String, Object> properties = configuration.getProperties();
		Enumeration<String> en = properties.keys();
		while (en.hasMoreElements()) {
			String prop = en.nextElement();
			System.out.println("  " + prop + " = " + properties.get(prop));
		}
	}

	private void updateConfig(String pid, String key, String value) throws IOException, InvalidSyntaxException {
		Configuration configuration = configAdmin.getConfiguration(pid, null);
		System.out.println("update configuration for pid " + pid);
		Dictionary<String, Object> properties = configuration.getProperties();
		if (properties == null) {
			properties = new Hashtable<>();
		}
		properties.put(key, value);
		configuration.update(properties);
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
