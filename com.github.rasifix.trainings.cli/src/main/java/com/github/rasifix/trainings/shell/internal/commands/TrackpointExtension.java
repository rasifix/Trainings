package com.github.rasifix.trainings.shell.internal.commands;

import groovy.lang.GroovyShell;
import aQute.bnd.annotation.component.Component;

@Component
public class TrackpointExtension implements GroovyShellExtension {

	@Override
	public void extend(GroovyShell shell) {
		shell.evaluate("com.github.rasifix.trainings.model.Trackpoint.metaClass.getSpeed = {-> def attr = getAttribute(com.github.rasifix.trainings.model.attr.SpeedAttribute.class); if (attr != null) return attr.value; return null; }");
		shell.evaluate("com.github.rasifix.trainings.model.Trackpoint.metaClass.getHr = {-> def attr = getAttribute(com.github.rasifix.trainings.model.attr.HeartRateAttribute.class); if (attr != null) return attr.value; return null; }");
	}

}
