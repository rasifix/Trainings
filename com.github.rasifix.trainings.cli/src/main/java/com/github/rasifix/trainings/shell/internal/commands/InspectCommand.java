package com.github.rasifix.trainings.shell.internal.commands;

import java.util.LinkedList;
import java.util.List;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class InspectCommand implements Command {

	private static final String NAME = "inspect";

	private List<GroovyShellExtension> extensions = new LinkedList<GroovyShellExtension>();
	
	@Reference(dynamic=true, unbind="removeExtension")
	public void addExtension(GroovyShellExtension extension) {
		this.extensions.add(extension);
	}
	
	public void removeExtension(GroovyShellExtension extension) {
		this.extensions.remove(extension);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Binding binding = new Binding();
		binding.setVariable("current", context.getCurrent());
		GroovyShell shell = new GroovyShell(binding);
		
		extend(shell);
				
		String command = context.getArgument(0);
		System.out.println(shell.evaluate(command));
		
		return context.getCurrent();
	}

	private void extend(GroovyShell shell) {
		for (GroovyShellExtension extension : extensions) {
			extension.extend(shell);
		}
	}

}
