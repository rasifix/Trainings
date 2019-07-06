package com.github.rasifix.solv.couch.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.lazycouch.CouchDatabase;
import com.github.rasifix.lazycouch.CouchServer;
import com.github.rasifix.lazycouch.Document;
import com.github.rasifix.lazycouch.LazyCouchFactory;
import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.EventKey;
import com.github.rasifix.solv.EventRepository;

@Component(properties={ "host=localhost", "port=5984" })
public class CouchEventRepository implements EventRepository {

	private String host;
	private int port;
	private CouchServer server;

	private LazyCouchFactory factory;
	
	@Reference
	public void setFactory(LazyCouchFactory factory) {
		this.factory = factory;
	}
	
	@Activate
	public void activate(ComponentContext context) {
		this.host = (String) context.getProperties().get("host");
		this.port = Integer.parseInt((String) context.getProperties().get("port"));
		this.server = factory.open(host, port);
	}
	
	@Override
	public EventKey addEvent(Event event) throws IOException {
		CouchDatabase db = this.server.getDatabase("solv");
		Document doc = toDocument(event);
		db.put(doc);
		return new CouchEventKey(new URL("http", host, port, "/solv"), doc.getString("_id"));
	}

	private Document toDocument(Event event) {
		JsonEventWriter writer = new JsonEventWriter();
		Map<String, Object> jsonEvent = writer.writeEvent(event);
		Document doc = new Document(generateUUID());
		doc.put("type", "orienteering.event");
		doc.set("event", jsonEvent);
		return doc;
	}

	private String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("[-]", "");
	}

	private static class CouchEventKey implements EventKey {

		private final URL base;
		private final String id;

		public CouchEventKey(URL url, String id) {
			this.base = url;
			this.id = id;
		}
		
		@Override
		public URL toURL() {
			try {
				return new URL(base, base.getFile() + "/" + id);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
