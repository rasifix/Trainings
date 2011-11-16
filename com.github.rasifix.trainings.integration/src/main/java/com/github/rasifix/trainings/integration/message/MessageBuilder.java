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
package com.github.rasifix.trainings.integration.message;

import java.util.HashMap;
import java.util.Map;

import com.github.rasifix.trainings.integration.Message;


public class MessageBuilder {

	private Map<String, Object> headers;
	
	private Object payload;
	
	public MessageBuilder() {
		this.headers = new HashMap<String, Object>();
	}
	
	private MessageBuilder(Message message) {
		this.headers = new HashMap<String, Object>(message.getHeaders());
	}
	
	public static MessageBuilder withMessage(Message message) {
		return new MessageBuilder(message);
	}
	
	public MessageBuilder setPayload(Object payload) {
		this.payload = payload;
		return this;
	}
	
	public MessageBuilder setHeader(String name, Object value) {
		this.headers.put(name, value);
		return this;
	}
	
	public Message build() {
		return new SimpleMessage(headers, payload);
	}

	public static MessageBuilder withPayload(Object payload) {
		return new MessageBuilder().setPayload(payload);
	}
	
}
