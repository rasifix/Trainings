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
package com.github.rasifix.trainings;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.github.rasifix.trainings.model.Activity;


public interface ActivityRepository {
	
	ActivityKey addActivity(Activity activity) throws IOException;

	void removeActivity(String activityId, String revision) throws IOException;
	
	Activity getActivity(String activityId) throws IOException;

	List<ActivityOverview> findActivities(Date startDate, Date endDate) throws IOException;
	
	interface ActivityOverview {
		
		Date getDate();
		
		String getSport();
		
		long getDuration();
		
		int getDistance();

		String getActivityId();
		
	}
	
}
