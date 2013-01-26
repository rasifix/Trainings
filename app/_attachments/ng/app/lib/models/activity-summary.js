require('trainings/core');

Trainings.ActivitySummary = Ember.Object.extend({
  loaded:false
});

Trainings.ActivitySummary.reopenClass({
  summaryByWeek: function(start, end) {
		var result = Trainings.ActivitySummary.create({
		  loaded : false
		});
		var startYear = start[0];
		var endYear = end[0];
    
    console.log("app/activitiesByWeek");
    console.log(start);
    console.log(end);
    
    $.couch.db('trainings').view("app/activitiesByWeek", {
      startkey: start,
		  endkey: end,
		  group_level: 3,
		  success: function(data) {
				result[startYear] = { "year" : startYear };
				result[endYear] = { "year" : endYear };
				
				result.weeks = [ ];
				
				var date = new Date();
				date.setDate(date.getDate() - 52 * 7);
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
				
				var end = new Date();
				end.setDate(end.getDate() + 1);
				end.setHours(0);
				end.setMinutes(0);
				end.setSeconds(0);
				
				while (date <= end) {
					var year = date.getYearOfWeek();
					console.log(year + "-" + date.getMonth());
					var weekrec = result[year][date.getWeek()];
					if (!weekrec) {
						weekrec = { };
						weekrec.week = date.getWeek();
						result[year][date.getWeek()] = weekrec;
						result.weeks.push([ year, date.getWeek() ]);
					}
					
					date.setDate(date.getDate() + 7);
				}
				
				if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var year = row.key[0];
						var week = row.key[1];
						var sport = row.value.sport;
						var wrec = result[year][week];	
						if (!wrec) {
							console.log(year + "-" + week + "(**)");
						} else {
							wrec[sport] = row.value;
						}
					});
				}
				
				console.log('-x-x-x-x-x-');
				console.log(result);
				result.set('loaded', true);
		  }
	  });
	  
	  return result;
  }
});