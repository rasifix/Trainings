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
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Message {
	
	protected static final byte PROTOCOL_VERSION = 1;

	public abstract byte[] toByteArray();

	protected static byte[] utf8(String text) {
		Charset charset = Charset.forName("UTF-8");
		return charset.encode(text).array();
	}
	
	protected static void write(ByteArrayOutputStream out, short value) {
		int high = (value >> 8) & 0xFF;
		int low  = value & 0xFF;
		out.write(high);
		out.write(low);
	}
	
	protected static byte[] md5(byte[] messageBytes, byte[] passwordBytes) {
	    try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(messageBytes, 0, messageBytes.length);
			
			if (passwordBytes.length > 0) {
				m.update(passwordBytes, 0, passwordBytes.length);
			}
			
			return m.digest();
			
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 is not supported by the platform", e);
		}
	}

}
