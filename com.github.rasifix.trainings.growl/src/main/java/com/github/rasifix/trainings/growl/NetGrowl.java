/*
 * Copyright 2011 Simon Raess
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rasifix.trainings.growl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.github.rasifix.trainings.growl.internal.Message;
import com.github.rasifix.trainings.growl.internal.NotificationMessage;
import com.github.rasifix.trainings.growl.internal.RegistrationMessage;


public class NetGrowl implements Growl {

	private static final int UDP_PORT = 9887;
	
	private final String applicationName;

	private final String password;
	
	private final InetSocketAddress socketAddress;

	public NetGrowl(String applicationName, String password) {
		this.applicationName = applicationName;
		this.password = password;
		this.socketAddress = new InetSocketAddress("127.0.0.1", UDP_PORT);
	}
	
	@Override
	public void registerApplication(String[] notifications) throws IOException {
		RegistrationMessage message = new RegistrationMessage();
		message.setApplicationName(applicationName);
		message.setNotifications(notifications);
		message.setPassword(password);
		send(message);
	}
	
	@Override
	public void sendNotification(String notificationName, String title, String description) throws IOException {
		NotificationMessage message = new NotificationMessage();
		message.setNotificationName(notificationName);
		message.setTitle(title);
		message.setDescription(description);
		message.setApplicationName(applicationName);
		message.setPassword(password);
		send(message);

	}

	private void send(Message message) throws IOException {
		byte[] bytes = message.toByteArray();
		DatagramSocket socket = new DatagramSocket();
		socket.connect(socketAddress);
		socket.send(new DatagramPacket(bytes, bytes.length));
		socket.close();
	}
	
	public static void main(String[] args) throws Exception {
		NetGrowl growl = new NetGrowl("FOO", "abc");
		growl.registerApplication(new String[] { "notified" });
	}

}
