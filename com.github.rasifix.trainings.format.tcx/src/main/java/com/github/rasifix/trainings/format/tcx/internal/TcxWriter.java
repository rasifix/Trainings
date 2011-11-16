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
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ALTITUDE_METERS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.AVERAGE_HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.BUILD_MAJOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.BUILD_MINOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.CALORIES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.CREATOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.DATE_FORMAT;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.DISTANCE_METERS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.INTENSITY;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LAP;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LATITUDE_DEGREES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.LONGITUDE_DEGREES;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAXIMUM_HEART_RATE_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.MAXIMUM_SPEED;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.NAME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.NAMESPACE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.POSITION;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.PRODUCT_ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.SENSOR_STATE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.SPORT;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.START_TIME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TIME;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TOTAL_TIME_SECONDS;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRACK;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRACKPOINT;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRAINING_CENTER_DATABASE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TRIGGER_METHOD;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TYPE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.TYPE_DEVICE;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.T_HEART_RATE_IN_BPM;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.UNIT_ID;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION_MAJOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.VERSION_MINOR;
import static com.github.rasifix.trainings.format.tcx.internal.TcxConstants.XSI_NAMESPACE;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.github.rasifix.trainings.format.tcx.internal.model.TcxActivity;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxCreator;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxDevice;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxLap;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxPosition;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrack;
import com.github.rasifix.trainings.format.tcx.internal.model.TcxTrackpoint;
import com.github.rasifix.trainings.format.tcx.internal.model.Version;

public class TcxWriter {

	private static Element createElement(final String name) {
		return new Element(name, NAMESPACE);
	}
	
	public String output(final TcxActivity activity) throws IOException {
		final Element root = createElement(TRAINING_CENTER_DATABASE);
		root.addNamespaceDeclaration(XSI_NAMESPACE);
		
		final Document doc = new Document(root);
		
		final Element activitiesEl = createElement(ACTIVITIES);
		activitiesEl.addContent(createActivity(activity));
		root.addContent(activitiesEl);
		
		final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		return outputter.outputString(doc);
	}
	
	public void output(final TcxActivity activity, final OutputStream out) throws IOException {
		output(Collections.singletonList(activity), out);
	}
	
	public void output(final List<TcxActivity> activities, final OutputStream out) throws IOException {
		final Element root = createElement(TRAINING_CENTER_DATABASE);
		root.addNamespaceDeclaration(XSI_NAMESPACE);
		
		final Document doc = new Document(root);
		
		final Element activitiesEl = createElement(ACTIVITIES);
		for (final TcxActivity activity : activities) {
			activitiesEl.addContent(createActivity(activity));
		}
		root.addContent(activitiesEl);
		
		final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		outputter.output(doc, out);
	}

	private Element createActivity(final TcxActivity activity) {
		final Element result = createElement(ACTIVITY);
		
		addOptionalAttribute(result, SPORT, activity.getSport());
		addOptionalElement(result, ID, activity.getId());
		
		for (final TcxLap lap : activity.getLaps()) {
			result.addContent(createLap(lap));
		}
		
		if (activity.getCreator() != null) {
			result.addContent(createCreator(activity.getCreator()));
		}
		
		return result;
	}
	
	private Element createCreator(final TcxCreator creator) {
		final Element result = createElement(CREATOR);
		
		addOptionalElement(result, NAME, creator.getName());
		
		if (creator instanceof TcxDevice) {
			final TcxDevice device = (TcxDevice) creator;
			result.setAttribute(TYPE, TYPE_DEVICE, XSI_NAMESPACE);			
			addOptionalElement(result, UNIT_ID, device.getUnitId());
			addOptionalElement(result, PRODUCT_ID, device.getProductId());
			addOptionalVersion(result, device.getVersion());
		}
		
		return result;
	}
	
	private Element createLap(final TcxLap lap) {
		final Element result = createElement(LAP);
		
		addOptionalAttribute(result, START_TIME, formatTime(lap.getStartTime()));
		addOptionalElement(result, TOTAL_TIME_SECONDS, formatNumber(lap.getTotalTime()));
		addOptionalElement(result, DISTANCE_METERS, formatNumber(lap.getDistance()));
		addOptionalElement(result, MAXIMUM_SPEED, formatNumber(lap.getMaximumSpeed()));
		addOptionalElement(result, CALORIES, toString(lap.getCalories()));
		
		addOptionalHR(result, AVERAGE_HEART_RATE_BPM, lap.getAverageHeartRate());
		addOptionalHR(result, MAXIMUM_HEART_RATE_BPM, lap.getMaximumHeartRate());
		
		addOptionalElement(result, INTENSITY, lap.getIntensity() != null ? lap.getIntensity().name() : null);
		addOptionalElement(result, TRIGGER_METHOD, lap.getTriggerMethod());
		
		for (final TcxTrack track : lap.getTracks()) {
			result.addContent(createTrack(track));
		}
		
		return result;
	}
	
	private static String toString(Object value) {
		return value == null ? null : value.toString();
	}

	private Element createTrack(final TcxTrack track) {
		final Element result = createElement(TRACK);
		for (final TcxTrackpoint trackpoint : track.getTrackpoints()) {
			result.addContent(createTrackpoint(trackpoint));
		}
		return result;
	}
	
	private Element createTrackpoint(final TcxTrackpoint trackpoint) {
		final Element result = createElement(TRACKPOINT);
		
		addOptionalElement(result, TIME, formatTime(trackpoint.getTime()));
		addOptionalPosition(result, trackpoint.getPosition());
		addOptionalElement(result, ALTITUDE_METERS, formatNumber(trackpoint.getAltitude()));
		addOptionalElement(result, DISTANCE_METERS, formatNumber(trackpoint.getDistance()));
		addOptionalHR(result, HEART_RATE_BPM, trackpoint.getHeartRate());
		addOptionalElement(result, SENSOR_STATE, trackpoint.getSensorState() != null ? trackpoint.getSensorState().name() : null);
		
		return result;
	}

	private void addOptionalAttribute(final Element element, final String attribute, final String value) {
		if (value != null) {
			element.setAttribute(attribute, value);
		}
	}
	
	private void addOptionalElement(final Element element, final String elementName, final String value) {
		if (value != null) {
			final Element child = createElement(elementName);
			child.setText(value);
			element.addContent(child);
		}
	}
	
	private void addOptionalElement(final Element element, final String elementName, final Short value) {
		if (value != null) {
			final Element child = createElement(elementName);
			child.setText(value.toString());
			element.addContent(child);
		}
	}
	
	private void addOptionalVersion(final Element element, final Version version) {
		if (version != null) {
			final Element versionEl = createElement(VERSION);
			addOptionalElement(versionEl, VERSION_MAJOR, version.getVersionMajor());
			addOptionalElement(versionEl, VERSION_MINOR, version.getVersionMinor());
			addOptionalElement(versionEl, BUILD_MAJOR, version.getBuildMajor());
			addOptionalElement(versionEl, BUILD_MINOR, version.getBuildMinor());
			element.addContent(versionEl);
		}
	}
	
	private void addOptionalHR(final Element element, final String elementName, final Integer value) {
		if (value != null) {
			final Element child = new Element(elementName, NAMESPACE);
			child.setAttribute(TYPE, T_HEART_RATE_IN_BPM, XSI_NAMESPACE);
			final Element valueEl = new Element("Value", NAMESPACE);
			valueEl.setText(Integer.toString(value));
			child.addContent(valueEl);
			element.addContent(child);
		}
	}
	
	private void addOptionalPosition(final Element element, final TcxPosition position) {
		if (position != null) {
			final Element child = new Element(POSITION, NAMESPACE);
			final Element latitudeEl = new Element(LATITUDE_DEGREES, NAMESPACE);
			latitudeEl.setText(formatNumber(position.getLatitude()));
			final Element longitudeEl = new Element(LONGITUDE_DEGREES, NAMESPACE);
			longitudeEl.setText(formatNumber(position.getLongitude()));
			child.addContent(latitudeEl);
			child.addContent(longitudeEl);
			element.addContent(child);
		}
	}
	
	private String formatTime(Date time) {
		if (time == null) return null;
		return new SimpleDateFormat(DATE_FORMAT).format(time);
	}
	
	private String formatNumber(Double value) {
		if (value == null) return null;
		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		format.setParseIntegerOnly(false);
		format.setMinimumFractionDigits(7);
		format.setMaximumFractionDigits(Integer.MAX_VALUE);
		String result = format.format(value);
		return result;
	}
	
}
