package com.github.rasifix.osgi.shell.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jline.ConsoleReader;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.application.Application;
import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

@Component(properties={ "appid=shell" })
public class Shell implements Application, CommandRegistry, CommandContext {

	private final Map<String, Command> commands = new HashMap<String, Command>();
	
	private final Map<Object, Object> context = new HashMap<Object, Object>();
	
	private final LinkedList<Object> stack = new LinkedList<Object>();
	
	private String[] arguments;

	private Object current;

	private ConsoleReader reader;

	private String wdir = ".";
	
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
	public void push(Object o) {
		stack.addLast(0);
	}
	
	@Override
	public Object peek() {
		return stack.getLast();
	}
	
	@Override
	public Object pop() {
		return stack.removeLast();
	}
	
	@Override
	public Iterator<Object> stackIterator() {
		return stack.iterator();
	}
	
	@Override
	public Object getCurrent() {
		return current;
	}
	
	@Override
	public void setCurrent(Object current) {
		this.current = current;
	}
	
	@Override
	public String getArgument(int idx) {
		return arguments[idx];
	}
	
	@Override
	public String[] getArguments() {
		return arguments;
	}
	
	@Override
	public void execute(String line) throws IOException {
		CommandLineParser parser = new SimpleCommandLineParser();
		String[] parts = parser.split(line);
		if (parts.length == 0) {
			return;
		}
		
		String commandName = parts[0]; 

		Command command = getCommand(commandName);
		if (command == null) {
			reader.printString("invalid command '" + commandName + "'");
			reader.printNewline();
			return;
		}
		
		try {
			arguments = Arrays.asList(parts).subList(1, parts.length).toArray(new String[0]);
			current = command.execute((CommandContext) this);
			push(current);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<File> resolveFiles(String fileName) {
		FileNameResolver resolver = new FileNameResolver(new File(wdir));
		return resolver.resolveFiles(fileName);
	}
	
	@Override
	public void start(String[] args) {
		try {
			PrintWriter debug = new PrintWriter(new File("console.log"));
			System.setProperty("file.encoding", "UTF-8");
			reader = new ConsoleReader();
			reader.setDebug(debug);
			reader.addCompletor(new CommandCompletor(this));
			
			String line = null;
			while ((line = reader.readLine("> ")) != null) {
				if ("exit".equals(line)) {
					break;
				}

				execute(line);
			}

			debug.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
