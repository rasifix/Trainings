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
package com.github.rasifix.trainings.ical.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.model.Activity;

public class ICalExporter implements ActivityExporter {

	@Override
	public URL exportActivity(Activity activity) throws IOException {
		Date startDate = activity.getStartTime();
		Date endDate = activity.getEndTime();
		String sport = activity.getSport();
		
		ProcessBuilder processBuilder = new ProcessBuilder(
				"/usr/bin/osascript", 
				"/Users/sir/.trainings/ical.scpt",
				format(startDate),
				format(endDate),
				sport);
		processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        System.out.println(line);
        
		return null;
	}

	private String format(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
		return format.format(date);
	}

}
