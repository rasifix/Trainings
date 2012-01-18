package com.github.rasifix.trainings.shell.internal;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jline.ConsoleReader;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.application.Application;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component(properties={ "appid=shell" })
public class Shell implements Application, CommandRegistry, CommandContext {

	private final Map<String, Command> commands = new HashMap<String, Command>();
	
	private final Map<Object, Object> context = new HashMap<Object, Object>();
	
	private String[] arguments;

	private Object current;
	
	@Reference(unbind="unregisterCommand", dynamic=true, multiple=true, optional=true)
	public void registerCommand(Command command) {
		synchronized (commands) {
			commands.put(command.getName(), command);
		}
	}
	
	public void unregisterCommand(Command command) {
		synchronized (commands) {
			commands.remove(command.getName());
		}
	}
	
	@Override
	public String[] getCommandNames() {
		synchronized (commands) {
			Set<String> commandNames = commands.keySet();
			return commandNames.toArray(new String[commandNames.size()]);
		}
	}
	
	@Override
	public Command getCommand(String name) {
		synchronized (commands) {
			return commands.get(name);
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		return context.containsKey(key);
	}
	
	@Override
	public Object get(Object key) {
		return context.get(key);
	}
	
	@Override
	public void put(Object key, Object value) {
		context.put(key, value);
	}
	
	@Override
	public Object getCurrent() {
		return current;
	}
	
	@Override
	public String[] getArguments() {
		return arguments;
	}
	
	@Override
	public void start(String[] args) {
		try {
			PrintWriter debug = new PrintWriter(new File("console.log"));
			ConsoleReader reader = new ConsoleReader();
			reader.setDebug(debug);
			reader.addCompletor(new CommandCompletor(this));
			
			String line = null;
			while ((line = reader.readLine("> ")) != null) {
				if ("exit".equals(line)) {
					break;
				}

				CommandLineParser parser = new SimpleCommandLineParser();
				String[] parts = parser.split(line);
				if (parts.length == 0) {
					continue;
				}
				
				String commandName = parts[0]; 
				reader.printString("executing " + commandName);
				reader.printNewline();

				Command command = getCommand(commandName);
				if (command == null) {
					reader.printString("invalid command '" + commandName + "'");
					reader.printNewline();
					continue;
				}
				
				reader.printString("command class = " + command.getClass().getName());
				reader.printNewline();
				
				try {
					arguments = Arrays.asList(parts).subList(1, parts.length).toArray(new String[0]);
					current = command.execute((CommandContext) this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			debug.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
