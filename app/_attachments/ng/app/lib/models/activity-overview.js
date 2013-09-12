require('trainings/core');

Trainings.ActivityOverview = Ember.Object.extend({});

Trainings.ActivityOverview.formatDate = function(date) {
  return date.getFullYear() + "-" + Trainings.pad(date.getMonth() + 1) + "-" + Trainings.pad(date.getDate()) + " 00:00:00";
}

Trainings.ActivityOverview.reopenClass({
  find: function(query) {
    var startkey = null;
    var endkey = null;
    var descending = query.descending || true;
    var limit = query.limit || 100;
    
    if (query.startDate && query.endDate) {
      startkey = Trainings.ActivityOverview.formatDate(query.startDate);
      endkey = Trainings.ActivityOverview.formatDate(query.endDate);
    } else {
      var month = query.month;
      var year = query.year;
      startkey = year + "-" + Trainings.pad(month) + "-32 00:00:00";
      endkey = year + "-" + Trainings.pad(month) + "-01 00:00:00";
    }
    		
		var result = Ember.ArrayProxy.create({ content: [] });
		result.set('loading', true);
    
    $.couch.db('trainings').view("app/overview", {
      endkey: endkey,
			startkey: startkey,
			descending: descending,
			limit: limit,
			success: function(data) {
			  result.set('loading', false);
	      if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;	
						var sport = row.value.sport;
						var sportUrl = "img/" + sport.toLowerCase() + ".svg";
						
					  var perfindex = null;
						if (row.value.alt) {
						  var realspeed = (row.value.distance + row.value.alt.gain * 10) / row.value.totalTime;
						
						  if (row.value.hr && row.value.hr.avg > 120) {
						    // 1 / (m / s) => s / m
						    // (s / m) * 1000 / 60 => min / km
						    perfindex = Math.round(1 / realspeed * (1000 / 60) * row.value.hr.avg); 
						  }
					  }
						
						result.pushObject(Trainings.ActivityOverview.create({
							id: row.id,
  						date: key.substring(0, 16),
							sport: sport,
							sportUrl: sportUrl,
							duration: Trainings.formatDuration(row.value.totalTime),
							distance: Trainings.formatDistance(row.value.distance),
							speed: Trainings.formatSpeedOrPace(row.value.sport, row.value.speed),
							altgain: row.value.alt ? row.value.alt.gain : null,
							altloss: row.value.alt ? row.value.alt.loss : null,
							perfindex: perfindex,
							avgHr: row.value.hr ? row.value.hr.avg : null
						}));
	        });
				}
			},
			error: function(e) {
			  console.log("you've got screwed");
			  console.log(e);
			  result.set('loading', false);
			}
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