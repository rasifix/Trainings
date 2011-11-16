package com.github.rasifix.trainings;

import java.io.IOException;

public interface Notifier {
	
	void sendNotification(String type, String title, String message) throws IOException;
	
}
