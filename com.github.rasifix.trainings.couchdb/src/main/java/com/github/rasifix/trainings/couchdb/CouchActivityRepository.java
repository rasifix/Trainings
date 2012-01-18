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

import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.couchdb.internal.CouchServerImpl;
import com.github.rasifix.trainings.format.json.JsonActivityReader;
import com.github.rasifix.trainings.format.json.JsonActivityWriter;
import com.github.rasifix.trainings.model.Activity;

public class CouchActivityRepository implements ActivityRepository, ActivityExporter {

	private String host;
	private int port;
	private CouchServerImpl server;

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
		view.setStartKey(format(startDate));
		view.setEndKey(format(endDate));
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
		Map<String, Object> jsonActivity = writer.writeActivity(activity);
		Document doc = new Document(generateUUID());
		doc.put("type", "activity");
		doc.put("activity", jsonActivity);
		return doc;
	}
	
	@Override
	public Activity getActivity(String activityId) throws IOException {
		CouchServer server = new CouchServerImpl("localhost", 5984);
		CouchDatabase db = server.getDatabase("trainings");
		Document doc = db.getById(activityId);
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

}
