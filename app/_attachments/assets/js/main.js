Trainings = Ember.Application.create({	
	showActivity: function(view, event, activity) {
		Trainings.activityController.set('activity', activity);
		this.showViewControllerModally(this.activityController);
	},
	
	showViewControllerModally: function(viewController) {
		var view = viewController.view();
		view.appendTo("#overlay");
		Ember.run.schedule("actions", null, function() {
			$("#overlay").fadeIn();
		});
	}
});

Trainings.ActivityListController = Ember.ArrayProxy.extend({
    content: [],
    loading: true,

	addActivity: function(activity) {
		var obj = Ember.Object.create(activity);
		this.pushObject(obj);
	}
});

Trainings.activityListController = Trainings.ActivityListController.create();

Trainings.activityListView = Ember.View.create({
    templateName: 'activity-list',
    activitiesBinding: 'Trainings.activityListController',
    loadingBinding: 'Trainings.activityListController.loading'
});

Trainings.activityListView.appendTo("#main");


hrToZone = function(hr) {
	if (hr < 120) {
		return "green";
	} else if (hr < 140) {
		return "yellow";
	} else if (hr < 160) {
		return "orange";
	} else {
		return "red";
	}
};

Trainings.ActivityController = Ember.Object.extend({
	activity: null,
		
	activityChanged: function() {
		if (this.activity) {
			var id = this.activity.id;
			console.log(id);
			Trainings.activityRepository.loadActivity(id, {
				success: function(activity) {
					var result = [ ];
					
					for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
						console.log("trackIdx = " + trackIdx);
						var track = activity.tracks[trackIdx];	
						
						var graph = [ ];			
						var path = [ ];
						
						console.log(track);
						console.log(track.trackpoints.length);

						var first = track.trackpoints[0];
						var hr = first.hr;
						if (hr === undefined) {
							continue;
						}
						
						var hrzone = hrToZone(hr);
						
						graph.push([first.elapsed / 1000, first.hr]);
						
						for (var i = 1; i < track.trackpoints.length; i+=5) {
							var trackpoint = track.trackpoints[i];
							var pos = trackpoint.pos;
							if (pos === undefined) {
								continue;
							}

							path.push([pos.lat, pos.lng]);
							
							if (trackpoint.hr === undefined) {
								continue;
							}
							
							graph.push([trackpoint.elapsed / 1000, trackpoint.hr]);

							var newzone = hrToZone(trackpoint.hr);
							if (hrzone != newzone) {
								result.push({
									hrzone: hrzone,
									path: path
								});
								
								path = [ ];
								path.push([pos.lat, pos.lng]);
								hrzone = newzone;
							}	
						}

						if (path.length >= 2) {
							result.push({
								hrzone: hrzone,
								path: path
							});
						}
					}
					
					Trainings.activityView.setMapData(result);
					Trainings.activityView.setGraphData(graph);
				}
			});
		}
	}
});

Trainings.activityController = Trainings.ActivityController.create({
	view: function() {
		return Trainings.activityView;
	}
});

// find out, why it does not work with other means...
Trainings.activityController.addObserver('activity', function() {
	this.activityChanged();
});

Trainings.activityView = Ember.View.create({
	templateName: 'activity',
	activityBinding: 'Trainings.activityController.activity',
	didInsertElement: function() {
		console.log(this.get('activity').get('sport'));
		var element = this.get('element');
		var options = {
	    	center: new google.maps.LatLng(0, 0),
			zoom: 8,
			mapTypeId: google.maps.MapTypeId.HYBRID,
			draggable: true
		};
		var mapDiv = document.getElementById("map");
		this.map = new google.maps.Map(mapDiv, options);
		
		this.graphDiv = document.getElementById("graph");
	},
	setMapData: function(mapData) {
		this.mapData = mapData;
		console.log(this.mapData);
		
		if (this.map == null) {
			return;
		}
		
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
			
			console.log("adding line");
			var line = new google.maps.Polyline({
				strokeColor: color,
				strokeOpacity: 0.5,
				strokeWeight: 6,
				path: path
			});
			line.setMap(this.map);
		}
		
		this.map.fitBounds(bounds);
	},
	setGraphData: function(graphData) {
		this.graphData = graphData;
		this.graph = new Dygraph(this.graphDiv, this.graphData, {
			width: 800,
			height: 200,
			highlightCircleSize: 6,
			interactionModel: Dygraph.Interaction.defaultModel,
			showRangeSelector: true
		});
	}
});

Trainings.ActivityRepository = Ember.Object.extend({
	
	pad: function(num) {
		return num < 10 ? '0' + num : '' + num;
	},
	
	loadActivities: function(year, month, callback) {
		console.log($.couch.db('trainings'));
		$.couch.db('trainings').view("app/overview", {
			success: function(data) {
				callback.success(data);
			},
			endkey: year + "-" + this.pad(month) + "-01 00:00:00",
			startkey: year + "-" + this.pad(month) + "-32 00:00:00",
			descending: true
		});
	},
	
	loadActivity: function(id, callback) {
		$.couch.db('trainings').openDoc(id, {
			success: function(data) {
				var activity = data.activity;
				callback.success(activity);
			}
		});
	}

});

Trainings.activityRepository = Trainings.ActivityRepository.create();

Ember.run(function() {	
	Trainings.activityListController.set('loading', true);
	Trainings.activityRepository.loadActivities(2012, 3, {
		success: function(data) {
			Trainings.activityListController.set('loading', false);
            if (data && data.rows && data.rows.length > 0) {
				data.rows.forEach(function(row) {
					var key = row.key;
					Trainings.activityListController.addActivity({
						id: row.id,
						date: key,
						sport: row.value.sport
					});
                });
			}
		}		
	});
});
