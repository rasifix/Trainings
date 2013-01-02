
loader.register('trainings/controllers', function(require) {
require('trainings/controllers/application');
require('trainings/controllers/activity');
require('trainings/controllers/activities');
require('trainings/controllers/activity-list');
require('trainings/controllers/dashboard');
require('trainings/controllers/equipments');
require('trainings/controllers/nav');
require('trainings/controllers/sport-summary');
});

loader.register('trainings/controllers/activities', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.ActivitiesController = Ember.ArrayController.extend({
  content: [],
  month: null,
  year: null,

	// calculated property to show month and year in title
	monthAndYear: function() {
	  var month = this.get('month');
	  var year = this.get('year');
	  return year + "-" + month;
  }.property('year', 'month'),
	
});

});

loader.register('trainings/controllers/activity-list', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.ActivityListController = Ember.ArrayController.extend({
  content: []
});

});

loader.register('trainings/controllers/activity', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.ActivityController = Ember.ObjectController.extend({
	activity: null,
	selection: null,
	series: [],
	primarySeries: null,
	secondarySeries: null,
	
	contentChanged: function() {
	  this._updateTracks(this.get("content"));
	  this._updateGraph(this.get("content"));
	}.observes('content.tracks'),
		
	_updateTracks: function(activity) {
		var result = { 
			paths: [], 
			lappoints: []
		};
		console.log("updating tracks");
		console.log(activity);
		if (!activity.get('tracks')) {
		  return result;
		}
		console.log("now for the action");
		for (var trackIdx = 0; trackIdx < activity.get('tracks').length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
				
			var path = [ ];

			var first = track.trackpoints[0];
			var hr = first.hr;
				
			var hrzone = hr === undefined ? null : Trainings.hrToZone(hr);
			
			for (var i = 0; i < track.trackpoints.length; i += 1) {
				var trackpoint = track.trackpoints[i];
				if (trackpoint.type === "lappoint" && trackpoint.pos) {
					result.lappoints.push([trackpoint.pos.lat, trackpoint.pos.lng]);
				}
			}
			
			for (var i = 1; i < track.trackpoints.length; i += 5) {
				var trackpoint = track.trackpoints[i];
				var pos = trackpoint.pos;
				if (pos === undefined) {
					continue;
				}

				path.push([pos.lat, pos.lng]);
					
				if (trackpoint.hr === undefined) {
					trackpoint.hr = null;
				}

				var newzone = Trainings.hrToZone(trackpoint.hr);
				if (hrzone != newzone) {
					result.paths.push({
						hrzone: hrzone,
						path: path
					});
						
					path = [ ];
					path.push([pos.lat, pos.lng]);
					hrzone = newzone;
				}	
			}

			if (path.length >= 2) {
				result.paths.push({
					hrzone: hrzone,
					path: path
				});
			}
		}
		
		console.log(result);
			
		this.set('mapdata', result);
	},
	
	_updateGraph: function(activity) {
	  console.log("_updateGraph");
	  
	  if (!activity.summary) {
	    return;
	  }
	  	  
	  var series = [ ];
		var index = 0;
		
		// hr series - if available
		if (activity.summary.hr) {
			series.pushObject({ 
				name:"hr", 
				index:index++,
				fillGraph:false,
				provider:function(trackpoint) { return trackpoint.hr; }
			});
		}
		
		// elevation series
		series.pushObject({ 
			name:"elevation", 
			index:index++,
			fillGraph:true,
			provider:function(trackpoint) { return trackpoint.alt; }
		});
		
		// cadence series - if available
		if (activity.summary.cadence) {
			series.pushObject({ 
				name:"cadence", 
				index:index++,
				fillGraph:false,
				provider:function(trackpoint) { return trackpoint.cadence; }
			});
		}
		
		var result = [ ];
	  
		var primarySeries = series[0];
		var secondarySeries = series[1];
				
		var config = {
			labels: ["time", primarySeries.name, secondarySeries.name, null]
	 	};
		config.y = {
			axis: { fillGraph: primarySeries.fillGraph }
		};
		config[secondarySeries.name] = {
			axis: { fillGraph: secondarySeries.fillGraph }
		};
	
		var graph = [ ];
		var trackpoints = [ ];			
		
		var convertTrackpoint = function(elapsed, trackpoint, track, idx) {
			var properties = [ ];
			properties.pushObject({
			  name: 'time',
			  value: Trainings.formatDuration(elapsed)
			});
			series.forEach(function(el) {
			  properties.pushObject({
			    name: el.name,
			    value: el.provider(trackpoint, track, idx)
			  });
			});
			return properties;		  
		};
		
		var offset = 0;
		for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
			
			var first = track.trackpoints[0];
			
			// get the values
			var primary = primarySeries.provider(first, track, 0);
			var secondary = secondarySeries.provider(first, track, 0);
			
			trackpoints.pushObject(convertTrackpoint(offset + first.elapsed / 1000, first, track, 0));
			
			graph.push([offset + first.elapsed / 1000, primary, secondary, first.pos]);
			
			for (var i = 1; i < track.trackpoints.length; i+=5) {
				var trackpoint = track.trackpoints[i];
				
				// get the values
				primary = primarySeries.provider(trackpoint, track, i);
				secondary = secondarySeries.provider(trackpoint, track, i);

  			trackpoints.pushObject(convertTrackpoint(offset + trackpoint.elapsed / 1000, trackpoint, track, i));
				
				graph.push([offset + trackpoint.elapsed / 1000, primary, secondary, trackpoint.pos]);
			}
			
			offset += track.trackpoints[track.trackpoints.length - 1].elapsed / 1000;
		}
		
		this.set('trackpoints', trackpoints);
		this.set('activity', activity);
		this.set('graphdata', graph);
		this.set('graphconfig', config);
	},
	
	selectionChanged: function(min, max) {
		// broken if there are multiple tracks :-S
		var selection = { };
		selection.duration = Trainings.formatDuration(max - min);
		
		var first = this.closestTrackpoint(min);
		var last = this.closestTrackpoint(max);
		
		if (first && last) {
			selection.distance = Trainings.formatDistance(last.distance - first.distance);
			selection.speed = Trainings.formatSpeedOrPace(this.activity.sport, (last.distance - first.distance) / (last.elapsed / 1000 - first.elapsed / 1000));
		}
		
		this.set('selection', selection);
	},
	
	closestTrackpoint: function(time) {
		var mindelta = 100000000;
		var result = null;
		for (var trackIdx = 0; trackIdx < this.activity.tracks.length; trackIdx++) {
			var track = this.activity.tracks[trackIdx];
			for (var trackpointIdx = 0; trackpointIdx < track.trackpoints.length; trackpointIdx++) {
				var trackpoint = track.trackpoints[trackpointIdx];
				if (trackpoint.elapsed) {
					var elapsed = trackpoint.elapsed / 1000;
					var delta = Math.abs(time - elapsed);
					if (delta < mindelta && trackpoint.distance) {
						mindelta = delta;
						result = trackpoint;
					}
				}
			}
		}
		return result;
	}
});

});

loader.register('trainings/controllers/application', function(require) {
require('trainings/core');

Trainings.ApplicationController = Ember.Controller.extend();

});

loader.register('trainings/controllers/dashboard', function(require) {
require('trainings/core');

Trainings.DashboardController = Ember.Controller.extend({
	graphData: { },
	show: function() {
		var now = new Date();
		
		var endYear = now.getYearOfWeek();
		var endWeek = now.getWeek();
		
		now.setDate(now.getDate() - 52 * 7);
		var startYear = now.getYearOfWeek();
		var startWeek = now.getWeek();

		this.set('graphData', Trainings.ActivitySummary.summaryByWeek([startYear, startWeek], [endYear, endWeek + 1]));
	}
});

});

loader.register('trainings/controllers/equipments', function(require) {
require('trainings/core');
require('trainings/models');

/*
 * Equipments Controller
 */
Trainings.EquipmentsController = Ember.ArrayProxy.extend({
  content: [],
  loading: true
});

});

loader.register('trainings/controllers/nav', function(require) {
require('trainings/core');

Trainings.NavController = Ember.Controller.extend({
  selected: null
});

});

loader.register('trainings/controllers/sport-summary', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.SportSummaryController = Ember.ArrayController.extend({
  content: []
});

});

loader.register('trainings/core', function(require) {
require('jquery');
require('handlebars');
require('ember');
require('ember-bootstrap');
require('trainings/ext');

/** gets the iso calendar week */
Date.prototype.getWeek = function () {
	// Create a copy of this date object
	var target  = new Date(this.valueOf());

	// ISO week date weeks start on monday
	// so correct the day number
	var dayNr   = (this.getDay() + 6) % 7;

	// ISO 8601 states that week 1 is the week
	// with the first thursday of that year.
	// Set the target date to the thursday in the target week
	target.setDate(target.getDate() - dayNr + 3);

	// Store the millisecond value of the target date
	var firstThursday = target.valueOf();

	// Set the target to the first thursday of the year
	// First set the target to january first
	target.setMonth(0, 1);
	// Not a thursday? Correct the date to the next thursday
	if (target.getDay() != 4) {
		target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
	}

	// The weeknumber is the number of weeks between the 
	// first thursday of the year and the thursday in the target week
	return 1 + Math.ceil((firstThursday - target) / 604800000); // 604800000 = 7 * 24 * 3600 * 1000
};

Date.prototype.getYearOfWeek = function() {
  var week = this.getWeek();
  var month = this.getMonth();
  var year = this.getFullYear();
  if (week === 1 && month === 11) {
    return year + 1;
  } else if (week >= 52 && month === 0) {
    return year - 1;
  } else {
    return year;
  }
}

Trainings = Ember.Application.create({
  VERSION: '0.1',
  pad: function(num) {
    return num < 10 ? '0' + num : '' + num;
  },
  pad100: function(num) {
    return num < 10 ? '00' + num : (num < 100 ? '0' + num : '' + num);
  },
  formatDistance: function(distance) {
  	var km = Math.floor(distance / 1000);
  	var m = Math.floor(distance % 1000);
  	return km + "." + this.pad100(m).substring(0, 1) + " km";
  },
  formatDuration: function(duration, minimal) {
  	var hours = Math.floor(duration / 3600);
  	var minutes = Math.floor((duration / 60) % 60);
  	var seconds = Math.floor(duration % 60);
  	if (minimal && hours === 0) {
  		return this.pad(minutes) + ":" + this.pad(seconds);
  	} else if (minimal && hours < 10) {
  		return hours + ":" + this.pad(minutes) + ":" + this.pad(seconds);
  	}
  	return this.pad(hours) + ":" + this.pad(minutes) + ":" + this.pad(seconds);
  },
  formatSpeedOrPace: function(sport, speed) {
  	if (sport == "CYCLING" || sport == "MTB-ORIENTEERING" || sport == "MTB") {
  		return this.formatSpeed(speed);
  	} else {
  		return this.formatPace(speed);
  	}
  },
  formatSpeed: function(speed) {
  	var kmh = (speed * 3.6).toFixed(1);
  	return kmh + " km/h";
  },
  formatPace: function(speed) {
  	var pace = (1 / speed) * 1000;
  	var m = Math.floor(pace / 60);
  	var s = Math.floor(pace % 60);
  	return this.pad(m) + ":" + this.pad(s) + " min/km";
  },
  hrToZone: function(hr) {
  	if (hr < 120) {
  		return "green";
  	} else if (hr < 140) {
  		return "yellow";
  	} else if (hr < 160) {
  		return "orange";
  	} else if (hr < 180) {
  		return "red";
  	} else {
  		return "#880000";
  	}
  }
});


});

loader.register('trainings/ext', function(require) {
var get = Ember.get, fmt = Ember.String.fmt;

Ember.View.reopen({
  templateForName: function(name, type) {
    if (!name) { return; }

    var templates = get(this, 'templates'),
        template = get(templates, name);

    if (!template) {
      try {
        template = require(name);
      } catch (e) {
        throw new Ember.Error(fmt('%@ - Unable to find %@ "%@".', [this, type, name]));
      }
    }

    return template;
  }
});

});

loader.register('trainings/main', function(require) {
require('trainings/core');
require('trainings/router');
require('trainings/controllers');
require('trainings/views');

Trainings.initialize();

});

loader.register('trainings/models', function(require) {
require('jquery-couch');
require('trainings/models/activity');
require('trainings/models/activity-overview');
require('trainings/models/activity-summary');
require('trainings/models/equipment');
require('trainings/models/sport-summary');

});

loader.register('trainings/models/activity-overview', function(require) {
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
});

loader.register('trainings/models/activity-summary', function(require) {
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
				
				result.set('loaded', true);
		  },
		  startkey: start,
		  endkey: end,
		  group_level: 3
	  });
	  
	  return result;
  }
});
});

loader.register('trainings/models/activity', function(require) {
require('trainings/core');

Trainings.Activity = Ember.Object.extend({
  id: null,
  sport: null,
  duration: null,
  distance: null,
  hr: null,
  tracks: Ember.ArrayProxy.extend({ content:[] })
});

Trainings.Activity.reopenClass({
	find: function(id) {
	  var result = Trainings.Activity.create();
	  result.id = id;
		$.couch.db('trainings').openDoc(id, {
			success: function(doc) {
			  var activity = doc.activity;
			  result.set('date', activity.date);
			  result.set('sport', activity.sport);
			  result.set('duration', Trainings.formatDuration(activity.summary.totalTime));
			  result.set('distance', Trainings.formatDistance(activity.summary.distance));
			  result.set('hr', activity.summary.hr);
			  result.set('summary', activity.summary);
			  result.set('tracks', activity.tracks);
			  console.log(result);
      },
      error: function(e) {
        console.log("sorry pal, you have a bad day");
      }
    });
    return result;
	}
});

});

loader.register('trainings/models/equipment', function(require) {
require('trainings/core');

Trainings.Equipment = Ember.Object.extend({ });

Trainings.Equipment.reopenClass({
	find: function() {
    var result = [];
    
		$.couch.db('trainings').view("app/equipments", {
			success: function(data) {
				$.couch.db('trainings').view("app/activitiesByEquipment", {
					reduce: true,
					group_level: 1,
					success: function(summaries) {
						var map = { };
						summaries.rows.forEach(function(row) {
							map[row.key[0]] = row.value.distance;
						});
						
						data.rows.forEach(function(row) {
							var distance = map[row.id];
							if (distance) {
								row.value.distance = Trainings.formatDistance(distance);
							}
							result.pushObject(Trainings.Equipment.create({
  							id: row.id,
  							name: row.value.name,
  							brand: row.value.brand,
  							dateOfPurchase: row.value.dateOfPurchase.substring(0, 10),
  							distance: row.value.distance
  						}));
						});
					}
				});
			}
		});
		
    return result;
	}
});

});

loader.register('trainings/models/sport-summary', function(require) {
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

});

loader.register('trainings/router', function(require) {
require('trainings/core');

Trainings.Router = Ember.Router.extend({
  enableLogging: true,
  
  root: Ember.Route.extend({
    gotoDashboard: Ember.Route.transitionTo('root.index'),
    gotoActivities: Ember.Route.transitionTo('root.activities.index'),
    gotoEquipments: Ember.Route.transitionTo('root.equipments'),

    showActivity: Ember.Route.transitionTo('activity'),	
    
    index: Ember.Route.extend({
      route: '/',
      connectOutlets: function(router, context) {
        router.set('navController.selected', 'dashboard');
        router.get('applicationController').connectOutlet('dashboard');
        router.get('dashboardController').show();
        router.get('dashboardController').connectOutlet({
          outletName: 'activities',
          viewClass: Trainings.ActivityListView,
          controller: Trainings.ActivityListController.create(),
          context: Trainings.ActivityOverview.query({ limit:5, descending:true })
        });
        router.get('dashboardController').connectOutlet({
          outletName: 'sportsummary',
          viewClass: Trainings.SportSummaryView,
          controller: Trainings.SportSummaryController.create(),
          context: Trainings.SportSummary.find()
        })
      }
    }),
    
    activities: Ember.Route.extend({
      route: '/activities',
      index: Ember.Route.extend({
        route: '/',
        connectOutlets: function(router, context) {
          console.log(context);
          var date = new Date();
          router.transitionTo('root.activities.show', { year:date.getFullYear(), month:date.getMonth() + 1 });
        }
      }),
      show: Ember.Route.extend({
		    route: '/:year/:month',
		    connectOutlets: function(router, context) {
          router.set('navController.selected', 'activities');
		      router.get('activitiesController').set('month', parseInt(context.month));
		      router.get('activitiesController').set('year', parseInt(context.year));
		      router.get('activitiesController').connectOutlet({
		        viewClass: Trainings.ActivityListView
		      });
          router.get('applicationController').connectOutlet('activities', Trainings.ActivityOverview.find(context));
		    },
		    		    
		    previous: function(router, context) {
          var newMonth = router.get('activitiesController.month') - 1;
          var newYear = router.get('activitiesController.year');
      		if (newMonth == 0) {
      			newMonth = 12;
      			newYear -= 1;
      		}
      		router.transitionTo("root.activities.show", { month: newMonth, year : newYear });
      	},

      	next: function(router) {
          var newMonth = router.get('activitiesController.month') + 1;
          var newYear = router.get('activitiesController.year');
      		if (newMonth == 13) {
      			newMonth = 1;
      			newYear += 1;
      		}
      		router.transitionTo("root.activities.show", { month: newMonth, year : newYear });
      	}
	    })
    }),
    
    activity: Ember.Route.extend({
      route: "/activity/:id",
      deserialize: function(router, context) {
        console.log("deserialize");
        console.log(context);
        return Trainings.Activity.find(context.id);
      },
      serialize: function(router, context) {
        console.log("serialize activity");
        console.log(context);
        console.log(context.id);
        return {
          id: context.id
        }
      },
      connectOutlets: function(router, activity) {
        console.log("---->");
        console.log(activity);
        router.set('navController.selected', 'activities');
        router.get('applicationController').connectOutlet('activity', Trainings.Activity.find(activity.id));
      }
    }),
    
    equipments: Ember.Route.extend({
      route: '/equipments',
      connectOutlets: function(router, context) {
        router.set('navController.selected', 'equipments');
        router.get('applicationController').connectOutlet('equipments', Trainings.Equipment.find());
      }
    })
  })
});

});

loader.register('trainings/views', function(require) {
require('trainings/views/application');
require('trainings/views/activity');
require('trainings/views/activities');
require('trainings/views/activity-list');
require('trainings/views/dashboard');
require('trainings/views/equipments');
require('trainings/views/nav');
require('trainings/views/sport-summary');

});

loader.register('trainings/views/activities', function(require) {
require('trainings/core');

Trainings.ActivitiesView = Ember.View.extend({
    templateName: 'trainings/~templates/activities'
});

});

loader.register('trainings/views/activity-list', function(require) {
require('trainings/core');

Trainings.ActivityListView = Ember.View.extend({
    templateName: 'trainings/~templates/activity-list'
});

});

loader.register('trainings/views/activity', function(require) {
require('trainings/core');

Trainings.ActivityView = Ember.View.extend({
	marker: null,
	templateName: 'trainings/~templates/activity',
	map: null,
	selectedTrackpoint: null,
	
	didInsertElement: function() {
		var element = this.get('element');
		var options = {
	    center: new google.maps.LatLng(0, 0),
			zoom: 8,
			mapTypeId: google.maps.MapTypeId.HYBRID,
			draggable: true
		};
		var mapDiv = document.getElementById("map");
		this.map = new google.maps.Map(mapDiv, options);
		
		var controlDiv = document.createElement("div");
		controlDiv.style.backgroundColor = 'white';
		google.maps.event.addListener(this.map, "mousemove", function(e) {
			controlDiv.innerText = e.latLng.lat() + "," + e.latLng.lng();
		});
		google.maps.event.addListener(this.map, "mouseout", function(e) {
			controlDiv.style.visibility = "hidden";
		});
		google.maps.event.addListener(this.map, "mouseover", function(e) {
			controlDiv.style.visibility = "visible";
		});
		this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(controlDiv);
		
		console.log("INVOKE MAP DATA CHANGED");
		this.mapdataChanged();
		
		this.graphDiv = document.getElementById("graph");
	},
	
	mapdataChanged: function() {
	  console.log("now display the map");	  
	  var mapData = this.get('controller.mapdata');
	  if (!mapData) {
	    console.log("no map data...");
	    return;
	  }
	  
	  console.log(mapData);
		this.mapData = mapData.paths;
		console.log(this.mapData);
		
		if (this.map === null) {
		  console.log("no map");
			return;
		}
		
		console.log("map data");

		var bounds = new google.maps.LatLngBounds();
		for (var idx = 0; idx < this.mapData.length; idx++) {
			var track = this.mapData[idx];
			var color = track.hrzone;
			
			var path = [];
			for (var tpIdx = 0; tpIdx < track.path.length; tpIdx++) {
				var tp = track.path[tpIdx];
				var latlng = new google.maps.LatLng(tp[0], tp[1]);
				path.push(latlng);
				bounds.extend(latlng);
			}
			
			var line = new google.maps.Polyline({
				strokeColor: color,
				strokeOpacity: 0.5,
				strokeWeight: 6,
				path: path
			});
			line.setMap(this.map);
		}

		console.log("number of laps = " + mapData.lappoints.length);
		for (var idx = 0; idx < mapData.lappoints.length; idx++) {
			var tp = mapData.lappoints[idx];
			var latlng = new google.maps.LatLng(tp[0], tp[1]);
			var marker = new google.maps.Marker({
				position: latlng,
				icon: new google.maps.MarkerImage(
					"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAAACXBIWXMAAAsTAAALEwEAmpwYAAAB6klEQVQokWWSvWsTYQDGn7uE2A5xuJSiiddgkyKihUpAjAqFQo1DXVoEoZODg/hBqP0zdHEpOAiu2sqBFSxUKioKwqG0fpUoqSaVavPV5HJ3yb3v+zhk7Pz8pt/v0UgCAJCOmQjrMzh1Oofp2Qz26sDyoo0f31agBUvYqJYAACTBtJHlpXOW3CpSOQ7pe6TvUTWblD8L5NUZi6ODWZIAU4bJyxcsVa+RUlI1m1TVClW9RrXXILsdqp0/5JWLFk8aJnhsIC9//yKlpPi6QffOTTpTE3Ry5+neukbx0e5t65/I4wN5HcnhnGbEQM9D9+EDaH19OHB7HpEbc0AohM79u6DTgm4OAYOJnI7r8xktHAKCLtT3LwiPT0C8egnx7Cn0xBGI1RdgqwWEwsDkVEaHroGKgBCg64KVXbBWAX0fbDTgSgEoBXR8wHWhY+GeDSGASASh9AjEuzfQUyPQk0ehtsuIjo5B6+8H223g8SMbHDby4vM6GQQU9ge2Z6dZiR9k8XCUzuRZBmurpO+xu2yRaSMPxg2TJ+KWLGz23NdrlOUS5VaRavcf6XsU79+SqZjFlGH2wiWNLMeGrOD1GmW51Gvwd4eysMmutdiDE9EsSWj7rnEokcOZ8QwCATx/YjeczkqVwVJq2y0BwH95A0ZwkTaTHQAAAABJRU5ErkJggg==", 
					null, null, new google.maps.Point(6,6)
				)
			});
			marker.setMap(this.map);
		}
		
		this.map.fitBounds(bounds);
	}.observes('controller.mapdata'),

	showPosition: function(pos) {
		if (pos === null) {
			if (this.marker !== null) {
				this.marker.setMap(null);
				this.marker = null;
			}
			return;
		}
		
		var latlng = new google.maps.LatLng(pos.lat, pos.lng);
		if (this.marker === null) {
			this.marker = new google.maps.Marker({
				position: latlng,
				icon: new google.maps.MarkerImage(
					"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMCAYAAABWdVznAAACAklEQVQoFR2SO2zTUBSGf79iNw8wxYrUIhfxKBioxMNFKAMSExkzZGZjZQDBVFYGBKrEwFwhBhBSqoYtYmGgAQGmNPSFIloHsKKqqZMArd+++PpIdzrf+c//61yGEAJa9fqcmiso1eLhI+VsrqC7jgPf2zfEEb6RMLWpqcu/UpAOvJp/Vlpe+rxg2zbxPI8EQZA+13VJr9cj7Y2NhXZ7vURZTtOOqmfPXXoweVKr5PN5RFGEMAwRx3EqmMvlIEqSNugPlG2zs8grxWJVHZ+oSJKEdt/DI1PEN5NBHIW4OOHj9vF9nFbykA+NVjb79ltWzIyWs9ksEht4vCVCEhjcnya4N82BB8HsmoPEJmRZplvLbFFRdZZlUyutRPn6QeDN7xjzP0OMSQLef99NByjDc7zOAwxoGOo5il3YHo/dgMBJIgyjGHGwl/ZpLt/3wXbMgUFhjuNwYdzFB9uBJhOcKkQY/h3iqkogCEIK2/aOwa60dhp2f4BMJoM7JxhEewO8aK7jdfML2P4Wbp0fgyiK6Ha7SaagwVsdv9YyflzTr3CVSUXGk1IGvn4gtUhFKGxZFtZWl+pCNKxxzY/P/zTf1TphFCuEsBoFKMgwDJLDwTRNtJY/1betzYc3bt5dZWhgWi/nZlX12Jkq4UbKQRjrJDmg6znGytfFRoB/tZmZp+nX+A/QHhMfMm7MxAAAAABJRU5ErkJggg==", 
					null, null, new google.maps.Point(6,6)
				)
			});
			this.marker.setMap(this.map);
		} else {
			this.marker.setPosition(latlng);
			this.map.setCenter(latlng); // follow selected trackpoint
		}
	},
	
	graphdataChanged: function() {
		if (this.graphDiv == null) {
		  console.log("no graph div found");
			return;
		}
		
		var self = this;
		this.graphData = this.get('controller.graphdata');
		config = this.get('controller.graphconfig') || { };
		config.width = 770;
		config.height = 200;
		config.labelsDiv = "legend";
		config.highlightCircleSize = 6;
		config.interactionModel = Dygraph.Interaction.defaultModel;
		config.showRangeSelector = true;
		config.highlightCallback = function(event, x, points, row, seriesName) {
		  var data = self.graphData[row];
		  var trackpoint = self.get('controller.trackpoints')[row];
		  self.set('selectedTrackpoint', trackpoint);
			var pos = data[3];
			if (pos !== undefined && pos !== null) {
				self.showPosition(pos);
			} else {
				self.showPosition(null);
			}
		};
		config.unhighlightCallback = function(e) {
			self.showPosition(null);
			self.set('selectedTrackpoint', null);
		};
		config.zoomCallback = function(min, max, yRanges) {
			self.get('controller').selectionChanged(min, max);
		};
		config.xLabelHeight = 12;
		config.colors = [ "red", "blue", "orange" ];
		config.fillAlpha = 0.3;
		config.axes = {
			x : {
				valueFormatter: Trainings.formatDuration,
				ticker: function(min, max, pixels, opts, dygraph, vals) {
					var pixelsPerLabel = 75;
					var maxTicks = pixels / pixelsPerLabel;
					var range = max - min;
					var minTickDelta = range / maxTicks;
					var candidates= [1, 2, 5, 10, 20, 30, 60, 120, 300, 600, 1200, 1800, 3600, 7200];
					var chosen;
					for (var idx in candidates) {
						if (minTickDelta < candidates[idx]) {
							chosen = candidates[idx];
							break;
						}
					}
					var start = min + (chosen - min % chosen);
					var result = [ ];
					for (var x = start; x < max; ) {
						result.push({ v: x, label: Trainings.formatDuration(x, true) });
						x += chosen;
					}
				  	return result;
				}
			}
		};
		
		if (this.graph != null) {
			console.log("destroy old graph");
			this.graph.destroy();
		}
		console.log("create dygraph");
		this.graph = new Dygraph(this.graphDiv, this.graphData, config);
	}.observes('controller.graphdata', 'controller.graphconfig')
});

});

loader.register('trainings/views/application', function(require) {
require('trainings/core');

Trainings.ApplicationView = Ember.View.extend({
  templateName: 'trainings/~templates/application'
});

});

loader.register('trainings/views/dashboard', function(require) {
require('trainings/core');

Trainings.DashboardView = Ember.View.extend({
	templateName: 'trainings/~templates/dashboard',
//	activitiesBinding: 'Trainings.dashboardController.activities',
	labels: [],
	
	didInsertElement: function() {
		// ignored for the moment
	},
	
	graphDataUpdated: function() {
		if (!this.get("element")) {
			console.log("too fast");
			return;
		}
		
		if (!this.get('controller.graphData.loaded')) {
		  console.log("graph data not yet loaded");
		  return;
		}
		
		var that = this;
		var data = this.get("controller.graphData");
		var div = document.getElementById("dashboard");
		var maxy = 0;
		
		var sumrec = function(wrec) {
			var sum = 0;
			for (var idx in wrec) {
				var next = wrec[idx];
				if (next.totalTime) {
					sum += next.totalTime;
				}
			}
			return sum;
		}
		
		console.log("now we've got this far, let's have some fun");
		console.log(div);
		console.log(data);
		
		var records = [];
		var paper = new Raphael(div, 940, 100);
		data.weeks.forEach(function(weekAndYear) {
			var week = data[weekAndYear[0]][weekAndYear[1]];
			var sum = sumrec(week);
			maxy = Math.max(maxy, sum);
			week.year = weekAndYear[0];
			records.push(week);
		});
				
		var width = 940.0 / records.length;
		var height = 100;
		var colors = [ "#5F04B4", "#5FB404", "#FE642E", "#40A2FF", "#2066FF" ];
				
		var time = function(wrec, sport) {
			var value = wrec[sport];
			if (value) {
				return value.totalTime;
			}
			return 0;
		}
		
		var rows = function(x, running, orienteering, cycling, mtb, mtbOrienteering, record) {
			var y = 0;
			var rect = paper.rect(x, y, Math.floor(width), running / maxy * height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[0]);
			rect.attr("stroke", "none");
			
			y += running / maxy * height;
			rect = paper.rect(x, y, width - 1, orienteering / maxy * height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[1]);
			rect.attr("stroke", "none");

			y += orienteering / maxy * height;
			rect = paper.rect(x, y, width - 1, cycling / maxy * height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[2]);
			rect.attr("stroke", "none");

			y += cycling / maxy * height;
			rect = paper.rect(x, y, width - 1, mtb / maxy * height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[3]);
			rect.attr("stroke", "none");

			y += mtb / maxy * height;
			rect = paper.rect(x, y, width - 1, mtbOrienteering / maxy * height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[4]);
			rect.attr("stroke", "none");
			
			rect = paper.rect(x, 0, width, height);
			rect.translate(0, height);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", "white");
			rect.attr("stroke", "none");
			rect.node.setAttributeNS(null, "class", "screen");	
			
			rect.hover(function(e) {
				var total = 0;
				var labels = [];
				labels.push({ "label":"Week", "duration":record.week + "-" + record.year });
				if (record["RUNNING"]) {
					total += record["RUNNING"].totalTime;
					labels.push({ "label":"Running", "duration":Trainings.formatDuration(record["RUNNING"].totalTime) });
				}
				if (record["ORIENTEERING"]) {
					total += record["ORIENTEERING"].totalTime;
					labels.push({ "label":"Orienteering", "duration":Trainings.formatDuration(record["ORIENTEERING"].totalTime) });
				}
				if (record["CYCLING"]) {
					total += record["CYCLING"].totalTime;
					labels.push({ "label":"Cycling", "duration":Trainings.formatDuration(record["CYCLING"].totalTime) });
				}
				if (record["MTB"]) {
					total += record["MTB"].totalTime;
					labels.push({ "label":"MTB", "duration":Trainings.formatDuration(record["MTB"].totalTime) });
				}
				if (record["MTB-ORIENTEERING"]) {
					total += record["MTB-ORIENTEERING"].totalTime;
					labels.push({ "label":"Bike-OL", "duration":Trainings.formatDuration(record["MTB-ORIENTEERING"].totalTime) });
				}
				labels.push({ "label":"Total", "duration":Trainings.formatDuration(total) });
				that.set('labels', labels);
				
			}, function(e) {
				that.set('labels', [ ]);
			});
		};
		
		for (var i = 0; i < records.length; i++) {
			var x = Math.round(i * width);
			var running = time(records[i], "RUNNING");
			var cycling = time(records[i], "CYCLING");
			var orienteering = time(records[i], "ORIENTEERING");
			var mtb = time(records[i], "MTB");
			var mtbOrienteering = time(records[i], "MTB-ORIENTEERING");
			rows(x, running, orienteering, cycling, mtb, mtbOrienteering, records[i]);
		}		
		
	}.observes("controller.graphData.loaded")
});

});

loader.register('trainings/views/equipments', function(require) {
require('trainings/core');

Trainings.EquipmentsView = Ember.View.extend({
    templateName: 'trainings/~templates/equipments'
});

});

loader.register('trainings/views/nav', function(require) {
require('trainings/core');

Trainings.NavView = Ember.View.extend({
  templateName: 'trainings/~templates/nav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),
    isActive: function() {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected')
  })
});
});

loader.register('trainings/views/sport-summary', function(require) {
require('trainings/core');

Trainings.SportSummaryView = Ember.View.extend({
    templateName: 'trainings/~templates/sport-summary'
});

});
