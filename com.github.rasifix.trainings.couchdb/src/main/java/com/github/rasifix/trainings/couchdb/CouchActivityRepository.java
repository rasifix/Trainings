package com.github.rasifix.trainings.couchdb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.couchdb.internal.CouchServerImpl;
import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.format.json.JsonActivityReader;
import com.github.rasifix.trainings.format.json.JsonActivityWriter;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;

@Component(properties={ "host=localhost", "port=5984" })
public class CouchActivityRepository implements ActivityRepository, ActivityExporter, EquipmentRepository {

	private String host;
	private int port;
	private CouchServerImpl server;

	@Activate
	public void activate(ComponentContext context) {
		this.host = (String) context.getProperties().get("host");
		this.port = Integer.parseInt((String) context.getProperties().get("port"));
		this.server = new CouchServerImpl(host, port);
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
		return new CouchActivityKey(new URL("http", host, port, "/trainings"), doc.getString("_id"));
	}
	
	@Override
	public List<ActivityOverview> findActivities(Date startDate, Date endDate) throws IOException {
		CouchDatabase db = server.getDatabase("trainings");
		CouchQuery view = db.createQuery("trainings", "overview");
		if (startDate != null) {
			view.setStartKey(format(startDate));
		}
		if (endDate != null) {
			view.setEndKey(format(endDate));
		}
		return view.query(new RowMapper<ActivityOverview>() {
			@Override
			public ActivityOverview mapRow(String id, Object key, Object value) {
				Date date = parseDate((String) key);
				return new ActivityOverviewImpl(id, date, (JsonObject) value);
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
		private final JsonObject value;

		public ActivityOverviewImpl(String id, Date date, JsonObject value) {
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
			return value.getString("sport");
		}

		@Override
		public long getDuration() {
			return value.getInt("totalTime");
		}

		@Override
		public int getDistance() {
			return (int) Math.round(value.getDouble("distance"));
		}
		
		@Override
		public Integer getAverageHeartRate() {
			if (value.containsKey("avgHr")) {
				return value.getInt("avgHr");
			} else if (value.containsKey("hr")) {
				JsonObject hr = value.getObject("hr");
				return hr.getInt("avg");
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
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	private Document toDocument(Activity activity) {
		JsonActivityWriter writer = new JsonActivityWriter();
		Map<String, Object> jsonActivity = writer.writeActivity(activity);
		Document doc = new Document(generateUUID());
		if (activity.getId() != null) {
			doc.put("_id", activity.getId());
		}
		if (activity.getRevision() != null) {
			doc.put("_rev", activity.getRevision());
		}
		doc.put("type", "activity");
		doc.put("activity", jsonActivity);
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
		return reader.readActivity(doc);
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
		CouchQuery view = db.createQuery("trainings", "equipments");
		return view.query(new RowMapper<Equipment>() {
			@Override
			public Equipment mapRow(String id, Object key, Object value) {
				JsonObject jsonObject = (JsonObject) value;
				return parseEquipment(id, jsonObject.getString("_rev"), jsonObject);
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
		
		JsonModelBuilder builder = new JsonModelBuilder();
		builder.startObject();
		builder.member("name", equipment.getName());
		builder.member("brand", equipment.getBrand());
		builder.member("dateOfPurchase", format(equipment.getDateOfPurchase()));
		builder.endObject();
		
		document.put("equipment", builder.getResult());
		
		return document;
	}
	
	private Equipment fromEquipmentDocument(Document document) {
		String type = document.getString("type");
		if (!"equipment".equals(type)) {
			throw new IllegalArgumentException("given document is not an equipment document");
		}
		
		JsonObject jsonObject = document.getObject("equipment");
		
		return parseEquipment(document.getId(), document.getRev(), jsonObject);
	}

	private Equipment parseEquipment(String id, String rev, JsonObject jsonObject) {
		Equipment equipment = new Equipment();
		equipment.setId(id);
		equipment.setRevision(rev);
		
		equipment.setName(jsonObject.getString("name"));
		equipment.setBrand(jsonObject.getString("brand"));
		if (jsonObject.get("dateOfPurchase") != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				equipment.setDateOfPurchase(format.parse(jsonObject.getString("dateOfPurchase")));
			} catch (ParseException e) {
				throw new IllegalArgumentException("cannot parse date", e);
			}
		}
		return equipment;
	}

}
