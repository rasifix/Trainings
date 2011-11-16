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

public class NotificationMessage extends Message {
	
	private static final byte TYPE = 1;
	
	private String notificationName;
	private String title;
	private String description;
	private String applicationName;
	private String password;

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(PROTOCOL_VERSION);
		out.write(TYPE);
		
		// flags - don't ask
		out.write(0);
		out.write(0);
		
//        int flags = (short) ((notification.priority & 0x07) * 2);
//        if (notification.priority < 0)
//            flags |= 0x08;
//        if (notification.sticky)
//            flags |= 0x01;
//        bout.write(flags >>> 8);
//        bout.write(flags & 0xff);
		
		// strings
		byte[] notificationBytes = utf8(notificationName);
		byte[] titleBytes = utf8(title);
		byte[] descriptionBytes = utf8(description);
		byte[] applicationBytes = utf8(applicationName);
		
		// notification name length
		write(out, (short) notificationBytes.length);
		
		// title length
		write(out, (short) titleBytes.length);
		
		// description length
		write(out, (short) descriptionBytes.length);
		
		// application name length
		write(out, (short) applicationBytes.length);
		
		// write out strings
		out.write(notificationBytes, 0, notificationBytes.length);
		out.write(titleBytes, 0, titleBytes.length);
		out.write(descriptionBytes, 0, descriptionBytes.length);
		out.write(applicationBytes, 0, applicationBytes.length);
		
		// checksum - including password
		byte[] checksum = md5(out.toByteArray(), utf8(password));
		out.write(checksum, 0, checksum.length);
		
		return out.toByteArray();
	}

}
