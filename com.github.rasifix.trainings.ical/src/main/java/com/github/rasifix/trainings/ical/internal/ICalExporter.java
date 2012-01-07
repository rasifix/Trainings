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
		
		StringBuilder builder = new StringBuilder("/usr/bin/osascript ");
		builder.append("/Users/sir/.trainings/ical.scpt ");
		builder.append(format(startDate)).append(" ");
		builder.append(format(endDate)).append(" ");
		builder.append(sport);
		
        Process process = Runtime.getRuntime().exec(builder.toString());
        
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
