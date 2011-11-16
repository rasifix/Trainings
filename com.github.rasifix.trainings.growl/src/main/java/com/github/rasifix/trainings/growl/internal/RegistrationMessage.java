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
package com.github.rasifix.trainings.growl.internal;

import java.io.ByteArrayOutputStream;


public class RegistrationMessage extends Message {
	
	private static final byte TYPE = 0;

	private String applicationName;
	
	private String password = "";
	
	private String[] notifications;

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public void setNotifications(String[] notifications) {
		this.notifications = notifications;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] appnameBytes = utf8(applicationName);

		out.write(PROTOCOL_VERSION);
		out.write(TYPE);
		write(out, (short) appnameBytes.length);
		
		// nall - number of notifications (byte)
		out.write(notifications.length);
		
		// ndef - number of notifications that are enabled per default (byte)
		out.write(notifications.length);
		
		// application name
		out.write(appnameBytes, 0, appnameBytes.length);
		
		// write notification names
		for (String notification : notifications) {
			byte[] notificationnameBytes = utf8(notification);
			write(out, (short) notificationnameBytes.length);
			out.write(notificationnameBytes, 0, notificationnameBytes.length);
		}
		
		// index to default messages (the current implementation marks all
		// notifications as enabled per default)
		for (int i = 0; i < notifications.length; i++) {
			out.write(i);
		}
		
		// checksum - including password
		byte[] checksum = md5(out.toByteArray(), utf8(password));
		out.write(checksum, 0, checksum.length);
		
		return out.toByteArray();
	}
	
}
