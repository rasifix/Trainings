
loader.register('trainings/model/activity-overview', function(require) {
require('trainings/core');
require('trainings/models');

App.ActivityOverview = Ember.Object.extend({
  find: function(query) {
    var month = query.month;
    var year = query.year;
    
    var overviews = [];
    $.couch.db('trainings').view("app/overview", {
			success: function(data) {
	      if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;						
						overviews.pushObject(Trainings.ActivityOverview.create({
							id: row.id,
							date: key,
							sport: row.value.sport,
							duration: formatDuration(row.value.totalTime),
							distance: formatDistance(row.value.distance),
							speed: formatSpeedOrPace(row.value.sport, row.value.speed),
							avgHr: row.value.hr ? row.value.hr.avg : null
						}));
	        });
				}
			},
			endkey: year + "-" + pad(month) + "-01 00:00:00",
			startkey: year + "-" + pad(month) + "-32 00:00:00",
			descending: true
		});
		
    return overviews;
  }
})
});
