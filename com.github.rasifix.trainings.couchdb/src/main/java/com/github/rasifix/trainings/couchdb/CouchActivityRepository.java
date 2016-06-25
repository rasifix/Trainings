package com.github.rasifix.trainings.couchdb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.osgi.service.component.ComponentContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.rasifix.lazycouch.CouchDatabase;
import com.github.rasifix.lazycouch.CouchQuery;
import com.github.rasifix.lazycouch.CouchServer;
import com.github.rasifix.lazycouch.Document;
import com.github.rasifix.lazycouch.LazyCouchFactory;
import com.github.rasifix.lazycouch.RowMapper;
import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.format.json.JsonActivityReader;
import com.github.rasifix.trainings.format.json.JsonActivityWriter;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(properties={ "host=localhost", "port=5984" })
public class CouchActivityRepository implements ActivityRepository, ActivityExporter, EquipmentRepository {

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
	public URL exportActivity(Activity activity) throws IOException {
		return addActivity(activity).toURL();
	}
	
	@Override
	public ActivityKey addActivity(Activity activity) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		Document doc = toDocument(activity);
		db.put(doc);
		return new CouchActivityKey(new URL("http", host, port, "/trainings"), doc.getId());
	}
	
	@Override
	public List<ActivityOverview> findActivities(Date startDate, Date endDate) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		CouchQuery view = db.createQuery("app", "overview");
		if (startDate != null) {
			view.setStartKey(format(startDate));
		}
		if (endDate != null) {
			view.setEndKey(format(endDate));
		}
		return view.query(new RowMapper<ActivityOverview>() {
			@Override
			public ActivityOverview mapRow(String id, Object key, Object value) {
				Date date = parseDate(((TextNode) key).asText());
				return new ActivityOverviewImpl(id, date, (ObjectNode) value);
			}
		});
	}
	
	public void removeActivity(String activityId, String revision) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		db.delete(activityId, revision);
	}
	
	protected static class ActivityOverviewImpl implements ActivityOverview {

		private final String id;
		private final Date date;
		private final ObjectNode value;

		public ActivityOverviewImpl(String id, Date date, ObjectNode value) {
			this.id = id;
			this.date = date;
			this.value = value;
		}
		
		@Override
		public String getActivityId() {
			return id;
		}

		@Override
		public Date getDate() {
			return date;
		}

		@Override
		public String getSport() {
			return value.path("sport").asText();
		}

		@Override
		public long getDuration() {
			return value.path("totalTime").asInt();
		}

		@Override
		public int getDistance() {
			return (int) Math.round(value.path("distance").asDouble());
		}
		
		@Override
		public Integer getAverageHeartRate() {
			if (value.has("avgHr")) {
				return value.path("avgHr").asInt();
			} else if (value.has("hr")) {
				JsonNode hr = value.path("hr");
				return hr.path("avg").asInt();
			}
			return null;
		}
		
	}

	private static Date parseDate(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}

	private Document toDocument(Activity activity) {
		JsonActivityWriter writer = new JsonActivityWriter();
		ObjectNode jsonActivity = writer.writeActivity(activity);
		Document doc = new Document(generateUUID());
		if (activity.getId() != null) {
			doc.put("_id", activity.getId());
		}
		if (activity.getRevision() != null) {
			doc.put("_rev", activity.getRevision());
		}
		doc.put("type", "activity");
		doc.set("activity", jsonActivity);
		return doc;
	}
	
	@Override
	public Activity getActivity(String activityId) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		Document doc = db.get(activityId);
		return fromDocument(doc);
	}
	
	private Activity fromDocument(Document doc) {
		JsonActivityReader reader = new JsonActivityReader();
		return reader.readActivity(doc.getNode());
	}

	private String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("[-]", "");
	}
	
	private static class CouchActivityKey implements ActivityKey {

		private URL base;
		private String id;

		public CouchActivityKey(URL base, String id) {
			this.base = base;
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
	
	@Override
	public String addEquipment(Equipment equipment) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		Document doc = toDocument(equipment);
		db.put(doc);
		equipment.setRevision(doc.getRev());
		return doc.getId();
	}
	
	@Override
	public Equipment getEquipment(String id) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		Document doc = db.get(id);
		return fromEquipmentDocument(doc);
	}
	
	@Override
	public List<Equipment> getAllEquipments() throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		CouchQuery view = db.createQuery("app", "equipments");
		return view.query(new RowMapper<Equipment>() {
			@Override
			public Equipment mapRow(String id, Object key, Object value) {
				ObjectNode jsonObject = (ObjectNode) value;
				return parseEquipment(id, jsonObject.path("_rev").asText(), jsonObject);
			}
		});
	}

	private Document toDocument(Equipment equipment) {
		Document document = new Document(generateUUID());
		document.put("type", "equipment");
		if (equipment.getId() != null) {
			document.put("_id", equipment.getId());
		}
		if (equipment.getRevision() != null) {
			document.put("_rev", equipment.getRevision());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("name", equipment.getName());
		node.put("brand", equipment.getBrand());
		node.put("dateOfPurchase", format(equipment.getDateOfPurchase()));
		
		document.set("equipment", node);
		
		return document;
	}
	
	private Equipment fromEquipmentDocument(Document document) {
		String type = document.path("type").asText();
		if (!"equipment".equals(type)) {
			throw new IllegalArgumentException("given document is not an equipment document");
		}
		
		ObjectNode jsonObject = (ObjectNode) document.path("equipment");
		
		return parseEquipment(document.getId(), document.getRev(), jsonObject);
	}

	private Equipment parseEquipment(String id, String rev, ObjectNode jsonObject) {
		Equipment equipment = new Equipment();
		equipment.setId(id);
		equipment.setRevision(rev);
		
		equipment.setName(jsonObject.path("name").asText());
		equipment.setBrand(jsonObject.path("brand").asText());
		if (jsonObject.get("dateOfPurchase") != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				equipment.setDateOfPurchase(format.parse(jsonObject.path("dateOfPurchase").asText()));
			} catch (ParseException e) {
				throw new IllegalArgumentException("cannot parse date", e);
			}
		}
		return equipment;
	}

}
