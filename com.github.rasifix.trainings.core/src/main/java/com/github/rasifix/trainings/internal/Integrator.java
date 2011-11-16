package com.github.rasifix.trainings.internal;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.github.rasifix.trainings.Notifier;
import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageHandler;
import com.github.rasifix.trainings.integration.SubscribableChannel;
import com.github.rasifix.trainings.integration.channel.SimpleSubscribableChannel;
import com.github.rasifix.trainings.integration.transformer.CollectionSplitter;
import com.github.rasifix.trainings.integration.transformer.PayloadTransformer;
import com.github.rasifix.trainings.integration.transformer.Transformer;


public class Integrator {
	
	private SubscribableChannel inbound;
	private Transformer importer;
	private Transformer processor;
	private Transformer exporter;
	private SubscribableChannel activitiesChannel;
	private SubscribableChannel activityChannel;
	private SubscribableChannel processedChannel;
	private SubscribableChannel exportedChannel;
	private PayloadTransformer importTransformer;
	private CollectionSplitter splitterTransformer;
	private PayloadTransformer processorTransformer;
	private PayloadTransformer exportTransformer;
	private Notifier notifier;
	private MessageHandler notifierHandler = new MessageHandler() {
		@Override
		public void onMessage(Message message) {
			@SuppressWarnings("unchecked")
			List<URL> urls = (List<URL>) message.getPayload();
			
			StringBuilder builder = new StringBuilder();
			for (URL url : urls) {
				builder.append(url.toString()).append("\n");
			}
			
			try {
				notifier.sendNotification("exported", "Exported Activity", builder.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public void setChannel(SubscribableChannel inbound) {
		this.inbound = inbound;
	}
	
	public void setImporter(Transformer transformer) {
		this.importer = transformer;
	}
	
	public void setProcessor(Transformer transformer) {
		this.processor = transformer;
	}

	public void setExporter(Transformer transformer) {
		this.exporter = transformer;
	}
	
	public void setNotifier(Notifier notifier) {
		this.notifier = notifier;
	}
	
	protected void activate() {
		activitiesChannel = new SimpleSubscribableChannel();
		activityChannel = new SimpleSubscribableChannel();
		processedChannel = new SimpleSubscribableChannel();
		exportedChannel = new SimpleSubscribableChannel();
		
		importTransformer = new PayloadTransformer();
		importTransformer.setInputChannel(inbound);
		importTransformer.setTransformer(importer);
		importTransformer.setOutputChannel(activitiesChannel);

		splitterTransformer = new CollectionSplitter();
		splitterTransformer.setInputChannel(activitiesChannel);
		splitterTransformer.setOutputChannel(activityChannel);
		
		processorTransformer = new PayloadTransformer();
		processorTransformer.setInputChannel(activityChannel);
		processorTransformer.setTransformer(processor);
		processorTransformer.setOutputChannel(processedChannel);
		        
		exportTransformer = new PayloadTransformer();
		exportTransformer.setInputChannel(activityChannel);
		exportTransformer.setTransformer(exporter);
		exportTransformer.setOutputChannel(exportedChannel);

		inbound.subscribe(importTransformer);
		activitiesChannel.subscribe(splitterTransformer);
		activityChannel.subscribe(processorTransformer);
		processedChannel.subscribe(exportTransformer);
		exportedChannel.subscribe(notifierHandler);
	}
	
	protected void deactivate() {
		inbound.unsubscribe(importTransformer);
		activitiesChannel.unsubscribe(splitterTransformer);
		activityChannel.unsubscribe(processorTransformer);
		processedChannel.unsubscribe(exportTransformer);
		exportedChannel.unsubscribe(notifierHandler);
	}
	
}
