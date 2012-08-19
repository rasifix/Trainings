package com.github.rasifix.trainings;

import java.io.IOException;
import java.util.List;

import com.github.rasifix.trainings.integration.resource.Resource;
import com.github.rasifix.trainings.model.Activity;

public interface ActivityImporter {

	List<Activity> importActivities(Resource resource) throws IOException;

}
