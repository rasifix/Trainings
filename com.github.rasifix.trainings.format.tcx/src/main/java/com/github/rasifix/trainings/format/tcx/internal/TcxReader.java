/* 
 * Copyright 2010 Simon Raess
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
package com.github.rasifix.trainings.format.tcx.internal;

import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ACTIVITIES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ACTIVITY;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ACTIVITY_EXTENSION_NAMESPACE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ALTITUDE_METERS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.AVERAGE_HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.AVERAGE_SPEED;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.AVG_RUN_CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.BIKE_CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.BUILD_MAJOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.BUILD_MINOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.CALORIES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.CREATOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.DISTANCE_METERS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.EXTENSIONS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.INTENSITY;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LAP;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LATITUDE_DEGREES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LONGITUDE_DEGREES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAXIMUM_HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAXIMUM_SPEED;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAX_BIKE_CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAX_RUN_CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.NAME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.NAMESPACE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.POSITION;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.PRODUCT_ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.RUN_CADENCE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.SENSOR_STATE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.SPEED;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.SPORT;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.START_TIME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.STEPS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TIME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TOTAL_TIME_SECONDS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRACK;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRACKPOINT;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRIGGER_METHOD;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TYPE_DEVICE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.UNIT_ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VALUE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION_MAJOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION_MINOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.XSI_NAMESPACE;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.github.rasifix.trainings.format.tcx.internal.model.Intensity;
import com.github.rasifix.trainings.format.tcx.internal.model.SensorState;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxCreator;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxDevice;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxLap;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxPosition;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrack;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrackpoint;
import com.github.rasifix.trainings.format.tcx.internal.model.Version;

public class TcxReader {	
	
	public List<TcxActivity> read(InputStream is) throws IOException {
		final SAXBuilder builder = new SAXBuilder();
		
		try {
			final Document document = builder.build(is);
			final Element root = document.getRootElement();
			final Element activities = getChild(root, ACTIVITIES);
			if (activities != null) {
				return readActivities(activities);
			}
			return new LinkedList<TcxActivity>();
			
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<TcxActivity> readActivities(final Element activitiesEl) throws Exception {
		final List<TcxActivity> result = new LinkedList<TcxActivity>();
		for (final Element activityEl : getChildren(activitiesEl, ACTIVITY)) {
			result.add(readActivity(activityEl));
		}
		return result;
	}
	
	public TcxActivity readActivity(final Element activityEl) throws Exception {
		final String id = getChildText(activityEl, ID);
		final String sport = activityEl.getAttributeValue(SPORT);

		final TcxActivity activity = new TcxActivity(id, sport);
		for (final Element lapEl : getChildren(activityEl, LAP)) {
			activity.addLap(readLap(lapEl));
		}
		
		activity.setCreator(readCreator(getChild(activityEl, CREATOR)));
		
		return activity;
	}
	
	public TcxCreator readCreator(final Element creatorEl) throws Exception {
		if (creatorEl == null) {
			return null;
		}
		
		final String type = creatorEl.getAttributeValue("type", XSI_NAMESPACE);
		if (TYPE_DEVICE.equals(type)) {
			final TcxDevice device = new TcxDevice();
			device.setName(getChildText(creatorEl, NAME));
			device.setUnitId(getChildText(creatorEl, UNIT_ID));
			device.setProductId(getChildText(creatorEl, PRODUCT_ID));
			device.setVersion(buildVersion(getChild(creatorEl, VERSION)));
			return device;
		}
		
		return null;
	}
	
	public TcxLap readLap(final Element lapEl) throws Exception {
		final TcxLap lap = new TcxLap(parseTime(lapEl.getAttributeValue(START_TIME)));
		
		lap.setTotalTime(Double.parseDouble(getChildText(lapEl, TOTAL_TIME_SECONDS)));
		lap.setDistance(Double.parseDouble(getChildText(lapEl, DISTANCE_METERS)));
		lap.setMaximumSpeed(parseDouble(lapEl, MAXIMUM_SPEED));
		lap.setCalories(parseShort(lapEl, CALORIES));
		lap.setAverageHeartRate(parseInteger(lapEl, join(AVERAGE_HEART_RATE_BPM, VALUE)));
		lap.setMaximumHeartRate(parseInteger(lapEl, join(MAXIMUM_HEART_RATE_BPM, VALUE)));
		lap.setAverageBikeCadence(parseShort(lapEl, BIKE_CADENCE));
		lap.setIntensity(parseIntensity(getChildText(lapEl, INTENSITY)));
		lap.setTriggerMethod(getChildText(lapEl, TRIGGER_METHOD));
		
		// extensions
		final Element extension = getChild(lapEl, EXTENSIONS);
		if (extension != null) {
			lap.setAverageSpeed(parseDouble(extension, AVERAGE_SPEED));
			lap.setMaximumBikeCadence(parseShort(extension, MAX_BIKE_CADENCE, ACTIVITY_EXTENSION_NAMESPACE));
			lap.setAverageRunCadence(parseShort(extension, AVG_RUN_CADENCE, ACTIVITY_EXTENSION_NAMESPACE));
			lap.setMaximumRunCadence(parseShort(extension, MAX_RUN_CADENCE, ACTIVITY_EXTENSION_NAMESPACE));
			lap.setSteps(parseInteger(extension, STEPS, ACTIVITY_EXTENSION_NAMESPACE));
		}
		
		// now the tracks
		for (final Element track : getChildren(lapEl, TRACK)) {
			lap.addTrack(readTrack(track));
		}
		
		return lap;
	}
	
	public TcxTrack readTrack(final Element trackEl) throws Exception {
		TcxTrackpoint trackpoint = null;
		TcxTrack track = new TcxTrack();
		for (Element trackpointEl : getChildren(trackEl, TRACKPOINT)) {				
			trackpoint = readTrackpoint(trackpoint, trackpointEl);
			track.addTrackpoint(trackpoint);
		}
		return track;
	}

	protected TcxTrackpoint readTrackpoint(TcxTrackpoint previous, Element trackpointEl) throws Exception {
		final TcxTrackpoint trackpoint = new TcxTrackpoint();
		trackpoint.setTime(parseTime(getChild(trackpointEl, TIME).getText()));
		
		final Double latitude = parseDouble(trackpointEl, join(POSITION, LATITUDE_DEGREES));
		final Double longitude = parseDouble(trackpointEl, join(POSITION, LONGITUDE_DEGREES));
		if (latitude != null && longitude != null) {
			trackpoint.setPosition(new TcxPosition(latitude, longitude));
		}
		
		trackpoint.setHeartRate(parseInteger(trackpointEl, join(HEART_RATE_BPM, VALUE)));
		
		Double distance = parseDouble(trackpointEl, DISTANCE_METERS);
		if (distance != null) {
			trackpoint.setDistance(parseDouble(trackpointEl, DISTANCE_METERS));
		} else if (previous == null) {
			trackpoint.setDistance(Double.valueOf(0));
		} else {
			trackpoint.setDistance(
					previous.getDistance() 
					+ calculateDistance(previous.getPosition(), trackpoint.getPosition()));
		}
		
		trackpoint.setAltitude(parseDouble(trackpointEl, ALTITUDE_METERS));
		trackpoint.setBikeCadence(parseShort(trackpointEl, CADENCE));
		trackpoint.setSensorState(parseSensorState(getChildText(trackpointEl, SENSOR_STATE)));
		
		// extensions
		final Element extensions = getChild(trackpointEl, EXTENSIONS);
		trackpoint.setRunCadence(parseShort(extensions, RUN_CADENCE, ACTIVITY_EXTENSION_NAMESPACE));
		trackpoint.setSpeed(parseDouble(extensions, SPEED, ACTIVITY_EXTENSION_NAMESPACE));
		
		return trackpoint;
	}
	
	
	// --> utility methods <--
	
	private static Version buildVersion(final Element versionEl) {
		final Short versionMajor = parseShort(versionEl, VERSION_MAJOR);
		final Short versionMinor = parseShort(versionEl, VERSION_MINOR);
		final Short buildMajor = parseShort(versionEl, BUILD_MAJOR);
		final Short buildMinor = parseShort(versionEl, BUILD_MINOR);
		return new Version(versionMajor, versionMinor, buildMajor, buildMinor);
	}
	
	private static Element getChild(Element parent, String childElementName) {
		return parent.getChild(childElementName, NAMESPACE);
	}
	
	@SuppressWarnings("unchecked")
	private static Iterable<Element> getChildren(Element element, String childElementName) {
		return element.getChildren(childElementName, NAMESPACE);
	}
	
	private static String getChildText(Element element, String childElementName) {
		return element.getChildText(childElementName, NAMESPACE);
	}
	
	private static Double parseDouble(Element element, String path) {
		return parseDouble(element, path, NAMESPACE);
	}
	
	private static Double parseDouble(Element element, String path, Namespace namespace) {
		Element current = getDescendant(element, path);		
		
		if (current != null) {
			return new Double(current.getTextTrim());
		}
		
		return null;
	}

	private static Element getDescendant(Element element, String path) {
		String[] steps = path.split("/");
		
		Element current = element;
		for (final String step : steps) {
			if (current == null) {
				break;
			}
			current = current.getChild(step, NAMESPACE);
		}
		
		return current;
	}
	
	private static Short parseShort(Element element, String childElementName) {
		return parseShort(element, childElementName, NAMESPACE);
	}
	
	private static Short parseShort(Element element, String childElementName, Namespace namespace) {
		if (element == null) return null;
		
		String value = element.getChildTextTrim(childElementName, namespace);
		return value != null ? new Short(value) : null;
	}
	
	private static Integer parseInteger(final Element element, final String path) {
		return parseInteger(element, path, NAMESPACE);
	}
	
	private static Integer parseInteger(final Element element, final String path, final Namespace namespace) {
		Element current = getDescendant(element, path);		
		
		if (current != null) {
			return new Integer(current.getTextTrim());
		}
		
		return null;
	}
	
	private static Intensity parseIntensity(final String value) {
		if (value == null) {
			return null;
		}
		
		try {
			return Intensity.valueOf(value);
		} catch (IllegalArgumentException e) {
			// not found, just return null;
			return null;
		}
	}
	
	private static SensorState parseSensorState(final String value) {
		if (value == null) {
			return null;
		}
		
		try {
			return SensorState.valueOf(value);
		} catch (IllegalArgumentException e) {
			// not found, just return null;
			return null;
		}
	}

	private static Date parseTime(final String value) throws Exception {
		if (value.length() == "yyyy-MM-ddTHH:mm:ssZ".length()) {
			final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return format.parse(value);
		} else {
			final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			return format.parse(value);
		}
	}
	
	private static String join(String first, String... paths) {
		final StringBuilder builder = new StringBuilder(first);
		for (String path : paths) {
			builder.append("/");
			builder.append(path);
		}
		return builder.toString();
	}
	
	public static double calculateDistance(TcxPosition p1, TcxPosition p2) {
		double radius = 6372797.560856; // mean earth radius
		double lat1 = p1.getLatitude();
		double lat2 = p2.getLatitude();
		double lon1 = p1.getLongitude();
		double lon2 = p2.getLongitude();
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		return radius * 2 * Math.asin(Math.sqrt(a));
	}
	
}
