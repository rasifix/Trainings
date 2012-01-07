package com.github.rasifix.trainings.cli;

import java.io.File;

import com.github.rasifix.trainings.application.Application;
import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageChannel;
import com.github.rasifix.trainings.integration.message.MessageBuilder;
import com.github.rasifix.trainings.integration.resource.FileResource;

public class ImportApplication implements Application {
	
	private MessageChannel inbound;
	
	public void setInbound(MessageChannel inbound) {
		this.inbound = inbound;
	}
	
	@Override
	public void start(String[] args) {
		for (int i = 1; i < args.length; i++) {
			File file = new File(args[i]);
			Message message = MessageBuilder.withPayload(new FileResource(file))
						                    .setHeader("file", file)
						                    .build();
			inbound.send(message);
		}
	}
	
}
