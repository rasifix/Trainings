require('trainings/core');

Trainings.ActivityOverview = Ember.Object.extend({});

Trainings.ActivityOverview.reopenClass({
  find: function(query) {
    var month = query.month;
    var year = query.year;
    		
		var result = Ember.ArrayProxy.create({ content: [] });
		result.set('loading', true);

    $.couch.db('trainings').view("app/overview", {
			success: function(data) {
			  result.set('loading', false);
	      if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;	
						var sport = row.value.sport;
						var sportUrl = "img/" + sport.toLowerCase() + ".svg";
						result.pushObject(Trainings.ActivityOverview.create({
							id: row.id,
  						date: key.substring(0, 16),
							sport: sport,
							sportUrl: sportUrl,
							duration: Trainings.formatDuration(row.value.totalTime),
							distance: Trainings.formatDistance(row.value.distance),
							speed: Trainings.formatSpeedOrPace(row.value.sport, row.value.speed),
							avgHr: row.value.hr ? row.value.hr.avg : null
						}));
	        });
				}
			},
			error: function(e) {
			  console.log("you've got screwed");
			  console.log(e);
			  result.set('loading', false);
			},
			endkey: year + "-" + Trainings.pad(month) + "-01 00:00:00",
			startkey: year + "-" + Trainings.pad(month) + "-32 00:00:00",
			descending: true
		});
		
    return result;
  },
  
  query: function(options) {
    var limit = options.limit || 5;
		var descending = options.descending || true;
		
		var result = Ember.ArrayProxy.create({ content: [] });
		result.set('loading', true);
		
		$.couch.db('trainings').view('app/overview', {
		  limit: limit,
		  descending: descending,
		  success: function(data) {
		    result.set('loading', false);
		    data.rows.forEach(function(row) {
					var key = row.key;	
					var sport = row.value.sport;
					var sportUrl = "img/" + sport.toLowerCase() + ".svg";
					result.pushObject(Trainings.ActivityOverview.create({
						id: row.id,
						date: key.substring(0, 16),
						sport: sport,
						sportUrl: sportUrl,
						duration: Trainings.formatDuration(row.value.totalTime),
						distance: Trainings.formatDistance(row.value.distance),
						speed: Trainings.formatSpeedOrPace(row.value.sport, row.value.speed),
						avgHr: row.value.hr ? row.value.hr.avg : null
					}));
				});
		  },
		  error: function(e) {
		    console.log("you've got screwed");
		    console.log(e);
		    result.set('loading', false);
		  }
		});
		return result;
  }
})