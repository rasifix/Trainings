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

import org.jdom.Namespace;

/**
 * Class holding literals for TCX parsing / writing.
 */
public final class TcxConstants {
	
	public static final String TRAINING_CENTER_DATABASE = "TrainingCenterDatabase";
	
	public static final String TYPE_DEVICE = "Device_t";

	public static final String ACTIVITY = "Activity";

	public static final String ACTIVITIES = "Activities";
	
	public static final String CREATOR = "Creator";
	
	public static final String ID = "Id";

	public static final String LAP = "Lap";

	public static final String SPORT = "Sport";

	public static final String TRACK = "Track";

	public static final String STEPS = "Steps";

	public static final String START_TIME = "StartTime";

	public static final String TOTAL_TIME_SECONDS = "TotalTimeSeconds";

	public static final String AVERAGE_HEART_RATE_BPM = "AverageHeartRateBpm";

	public static final String MAXIMUM_HEART_RATE_BPM = "MaximumHeartRateBpm";

	public static final String TRIGGER_METHOD = "TriggerMethod";

	public static final String MAXIMUM_SPEED = "MaximumSpeed";
	
	public static final String CALORIES = "Calories";
	
	public static final String INTENSITY = "Intensity";

	public static final String AVERAGE_SPEED = "AverageSpeed";

	public static final String SPEED = "Speed";

	public static final String RUN_CADENCE = "RunCadence";

	public static final String TIME = "Time";

	public static final String VALUE = "Value";

	public static final String TRACKPOINT = "Trackpoint";
	
	public static final String LONGITUDE_DEGREES = "LongitudeDegrees";

	public static final String LATITUDE_DEGREES = "LatitudeDegrees";

	public static final String POSITION = "Position";

	public static final String HEART_RATE_BPM = "HeartRateBpm";

	public static final String DISTANCE_METERS = "DistanceMeters";

	public static final String ALTITUDE_METERS = "AltitudeMeters";

	public static final String CADENCE = "Cadence";
	
	public static final String SENSOR_STATE = "SensorState";

	public static final String BIKE_CADENCE = "Cadence";

	public static final String MAX_BIKE_CADENCE = "MaxBikeCadence";
	
	public static final String AVG_RUN_CADENCE = "AvgRunCadence";

	public static final String MAX_RUN_CADENCE = "MaxRunCadence";

	public static final String EXTENSIONS = "Extensions";

	// creator
	public static final String NAME = "Name";
	public static final String UNIT_ID = "UnitId";
	public static final String PRODUCT_ID = "ProductID";
	
	// version
	public static final String VERSION = "Version";
	public static final String VERSION_MAJOR = "VersionMajor";
	public static final String VERSION_MINOR = "VersionMinor";
	public static final String BUILD_MAJOR = "BuildMajor";
	public static final String BUILD_MINOR = "BuildMinor";
	
	// namespaces
	public static final Namespace NAMESPACE = Namespace.getNamespace("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2");
	
	public static final Namespace ACTIVITY_EXTENSION_NAMESPACE = Namespace.getNamespace("http://www.garmin.com/xmlschemas/ActivityExtension/v1");
	
	// XSI information
	public static final Namespace XSI_NAMESPACE = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	public static final String TYPE = "type";
	public static final String T_HEART_RATE_IN_BPM = "HeartRateInBeatsPerMinute_t";
	
	// misc
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

}
