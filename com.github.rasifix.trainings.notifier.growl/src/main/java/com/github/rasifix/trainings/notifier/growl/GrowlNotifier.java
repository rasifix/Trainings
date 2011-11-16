package com.github.rasifix.trainings.notifier.growl;

import java.io.IOException;


import org.osgi.service.component.ComponentContext;

import com.github.rasifix.trainings.Notifier;
import com.github.rasifix.trainings.growl.Growl;
import com.github.rasifix.trainings.growl.NetGrowl;

public class GrowlNotifier implements Notifier {

	private static final String[] NOTIFICATIONS = { "exported" };
	
	private Growl growl;
	
	public void activate(ComponentContext context) throws IOException {
		String application = (String) context.getProperties().get("application");
		String password = (String) context.getProperties().get("password");
		growl = new NetGrowl(application, password);
		growl.registerApplication(NOTIFICATIONS);
	}
	
	@Override
	public void sendNotification(String type, String title, String message) throws IOException {
		growl.sendNotification(type, title, message);
	}

}
