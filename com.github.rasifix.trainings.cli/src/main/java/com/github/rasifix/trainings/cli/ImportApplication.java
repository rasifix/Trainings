package com.github.rasifix.trainings.cli;

import java.io.File;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.application.Application;
import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageChannel;
import com.github.rasifix.trainings.integration.message.MessageBuilder;
import com.github.rasifix.trainings.integration.resource.FileResource;

@Component(properties={ "appid=import" })
public class ImportApplication implements Application {
	
	private MessageChannel inbound;
	
	@Reference
	public void setInbound(MessageChannel inbound) {
		this.inbound = inbound;
	}
	
	@Override
	public void start(String[] args) {
		for (int i = 1; i < args.length; i++) {
			File file = new File(args[i]);
			System.out.println("importing " + file.getAbsolutePath());
			Message message = MessageBuilder.withPayload(new FileResource(file))
						                    .setHeader("file", file)
						                    .build();
			inbound.send(message);
		}
	}
	
}
