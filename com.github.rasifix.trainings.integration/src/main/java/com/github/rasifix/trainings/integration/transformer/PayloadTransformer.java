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
package com.github.rasifix.trainings.integration.transformer;


import com.github.rasifix.trainings.integration.Message;
import com.github.rasifix.trainings.integration.MessageHandler;
import com.github.rasifix.trainings.integration.message.MessageBuilder;

public class PayloadTransformer extends AbstractTransformerHandler implements MessageHandler {

	private Transformer transformer;
	
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public void onMessage(Message message) {
		Object payload = message.getPayload();
		Object transformed = transformer.transform(payload);
		Message outgoingMessage = MessageBuilder.withMessage(message).setPayload(transformed).build();
		outputChannel.send(outgoingMessage);
	}

}
