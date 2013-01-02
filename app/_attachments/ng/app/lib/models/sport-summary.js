http://localhost:5984/_utils/database.html?trainings/_design/app/_view/activitiesBySport

require('trainings/core');

Trainings.SportSummary = Ember.Object.extend({ });

Trainings.SportSummary.reopenClass({
  find: function(query) {
    var query = query || { };
    var year = query.year || new Date().getFullYear();
    
    var result = Ember.ArrayProxy.create({ content:[] });
    result.set('loading', true);
    
    $.couch.db('trainings').view("app/activitiesBySport", {
			success: function(data) {
			  console.log('SportSummary.find');
			  console.log(data);
			  result.set('loading', false);
	      if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;	
						var sport = key[0];
						if (key[1] === year) {
						  var sportUrl = "img/" + sport.toLowerCase() + ".svg";
						  result.pushObject(Trainings.SportSummary.create({
							  id: row.id,
							  sport: sport,
							  sportUrl: sportUrl,
							  count: row.value.count,
							  duration: Trainings.formatDuration(row.value.totalTime),
							  distance: Trainings.formatDistance(row.value.distance),
							  speed: Trainings.formatSpeedOrPace(sport, row.value.speed)
						  }));
	          }
          });
				}
			},
			error: function(e) {
			  console.log("you've got screwed");
			  console.log(e);
			  result.set('loading', false);
			},
			reduce: true,
			group_level: 2
		});
		
    return result;
  }
});
