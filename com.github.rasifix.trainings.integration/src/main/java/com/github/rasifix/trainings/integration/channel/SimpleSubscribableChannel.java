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
package com.github.rasifix.trainings.integration.channel;

import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageHandler;
import com.github.rasifix.trainings.integration.SubscribableChannel;


public class SimpleSubscribableChannel implements SubscribableChannel {

	private final List<MessageHandler> subscribers = new LinkedList<MessageHandler>();
	
	@Override
	public void send(Message message) {
		for (MessageHandler subscriber : subscribers) {
			subscriber.onMessage(message);
		}
	}

	@Override
	public void subscribe(MessageHandler handler) {
		this.subscribers.add(handler);
	}

	@Override
	public void unsubscribe(MessageHandler handler) {
		this.subscribers.remove(handler);
	}

}
