// create application object
Trainings = Ember.Application.create({	
	showActivity: function(activity) {
		console.log("showActivity " + activity);
		window.location.hash = "#activity:" + activity;
	},
	showActivities: function() {
		window.location.hash = "#activities:" + Trainings.activityListController.get('year') + pad(Trainings.activityListController.get('month'));
	}
});

Trainings.ActivityListController = Ember.ArrayProxy.extend({
    content: [],
    loading: true,
    month: null,
    year: null,

	// calculated property to show month and year in title
	monthAndYear: function() {
	    var month = this.get('month');
	    var year = this.get('year');
	    return year + "-" + month;
    }.property('year', 'month'),

	addActivity: function(activity) {
		var obj = Ember.Object.create(activity);
		this.pushObject(obj);
	},
	
	previous: function() {
		console.log("PREVIOUS");
		
		var newYear = this.get('year');
		var newMonth = this.get('month') - 1;
		if (newMonth == 0) {
			newMonth = 12;
			newYear -= 1;
		}
		window.location.hash = "#activities:" + newYear + pad(newMonth);
	},
	
	next: function() {
		var newYear = this.get('year');
		var newMonth = this.get('month') + 1;
		if (newMonth == 13) {
			newMonth = 1;
			newYear += 1;
		}
		window.location.hash = "#activities:" + newYear + pad(newMonth);
	},
	
	showMonth: function(month, year) {
		this.set('month', month);
		this.set('year', year);
		this.loadActivities();
	},
	
	loadActivities: function() {
		this.set('loading', true);
		this.set('content', []);
		var that = this;
		Trainings.activityRepository.loadActivities(this.get('year'), this.get('month'), {
			success: function(data) {
				that.set('loading', false);
	            if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;
						that.addActivity({
							id: row.id,
							date: key,
							sport: row.value.sport,
							duration: formatDuration(row.value.totalTime),
							distance: formatDistance(row.value.distance),
							speed: "CYCLING" == row.value.sport ? formatSpeed(row.value.speed) : formatPace(row.value.speed),
							avgHr: row.value.hr ? row.value.hr.avg : null
						});
	                });
				}
			}		
		});
	}
});

Trainings.activityListController = Trainings.ActivityListController.create();

Trainings.activityListView = Ember.View.create({
    templateName: 'activity-list',
    activitiesBinding: 'Trainings.activityListController',
    loadingBinding: 'Trainings.activityListController.loading',
	monthAndYearBinding: 'Trainings.activityListController.monthAndYear'
});


/*
 * Equipment List Controller + View
 */
Trainings.EquipmentListController = Ember.ArrayProxy.extend({
    content: [],
    loading: true,

	addEquipment: function(equipment) {
		var obj = Ember.Object.create(equipment);
		this.pushObject(obj);
	},

	loadEquipments: function() {
		this.set('loading', true);
		this.set('content', []);
		var that = this;
		Trainings.activityRepository.loadEquipments({
			success: function(data) {
				that.set('loading', false);
	            if (data && data.rows && data.rows.length > 0) {
					data.rows.forEach(function(row) {
						var key = row.key;
						that.addEquipment({
							id: row.id,
							name: row.value.name,
							brand: row.value.brand,
							dateOfPurchase: row.value.dateOfPurchase.substring(0, 10),
							distance: row.value.distance
						});
	                });
				}
			}		
		});
	}
});

Trainings.equipmentListController = Trainings.EquipmentListController.create();

Trainings.equipmentListView = Ember.View.create({
    templateName: 'equipment-list',
    equipmentsBinding: 'Trainings.equipmentListController',
    loadingBinding: 'Trainings.equipmentListController.loading'
});

hrToZone = function(hr) {
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
};

Trainings.ActivityController = Ember.Object.extend({
	activity: null,
	summary: null,
	selection: null,
	series: [],
	primarySeries: null,
	secondarySeries: null,
	
	showActivity: function(id) {
		var that = this;
		Trainings.activityRepository.loadActivity(id, {
			success: function(activity) {
				// get available series
				var series = [ ];
				var index = 0;
				if (activity.summary.hr) {
					series.pushObject({ 
						name:"hr", 
						index:index++,
						fillGraph:false,
						provider:function(trackpoint) { return trackpoint.hr; }
					});
				}
				
				series.pushObject({ 
					name:"elevation", 
					index:index++,
					fillGraph:true,
					provider:function(trackpoint) { return trackpoint.alt; }
				});
				
				if (activity.summary.cadence) {
					series.pushObject({ 
						name:"cadence", 
						index:index++,
						fillGraph:false,
						provider:function(trackpoint) { return trackpoint.cadence; }
					});
				}
				series.pushObject({ 
					name:"speed", 
					index:index++,
					fillGraph:false,
					provider:function(curr, track, idx) { 
						var windowSize = 15;
						if (idx < windowSize || idx > track.trackpoints.length - windowSize) {
							return null;
						}
						var prev = track.trackpoints.get(idx - windowSize);
						var next = track.trackpoints.get(idx + windowSize);
						var dist = next.distance - prev.distance;
						var time = next.elapsed - prev.elapsed;
						if (time == 0) {
							return null;
						}
						if (track.sport == 'CYCLING' || track.sport == 'MTB') {
							return dist / (time / 1000) * 3.6;
						} else {
							// s/m -> min / km --> x / 60 * 1000
							return (time / 1000) / dist * 1000 / 60;
						}
					}
				});
				
				console.log(activity.tracks[0].sport);
								
				var result = [ ];
								
				var config = { };
				
				var summary = { };
				summary.date = activity.date;
				summary.sport = activity.summary.sport;
				summary.distance = formatDistance(activity.summary.distance);
				summary.duration = formatDuration(activity.summary.totalTime);
				summary.speed = "CYCLING" == activity.summary.sport ? formatSpeed(activity.summary.speed) : formatPace(activity.summary.speed);
				summary.hr = activity.summary.hr;
				summary.cadence = activity.summary.cadence;
				that.set('summary', summary);
				
				Ember.changeProperties(function() {
					// list of available series - used by series select boxes
					that.set("series", series);

					// save activity for later use
					that.set('activity', activity);
					that.set('primarySeries', series[0]);
					that.set('secondarySeries', series[1]);
					that.updateTracks_(activity);
					
					// happens by updating the series
					//that.updateGraph_(activity);
				});
			}
		});
	},
	
	seriesChanged: function() {
		var activity = this.get('activity');
		var primarySeries = this.get('primarySeries');
		var secondarySeries = this.get('secondarySeries');
		this.updateGraph_(activity);
	}.observes("primarySeries", "secondarySeries"),
	
	updateTracks_: function(activity) {
		var result = [];
		for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
				
			var path = [ ];

			var first = track.trackpoints[0];
			var hr = first.hr;
				
			var hrzone = hr === undefined ? null : hrToZone(hr);
				
			for (var i = 1; i < track.trackpoints.length; i+=5) {
				var trackpoint = track.trackpoints[i];
				var pos = trackpoint.pos;
				if (pos === undefined) {
					continue;
				}

				path.push([pos.lat, pos.lng]);
					
				if (trackpoint.hr === undefined) {
					trackpoint.hr = null;
				}

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
	},
	
	updateGraph_: function(activity) {
		var series = this.get('series');
		var primarySeries = this.get('primarySeries');
		var secondarySeries = this.get('secondarySeries');
		
		if (series.length == 0 || primarySeries == null || secondarySeries == null) {
			return;
		}
		
		console.log("updateGraph");
		console.log(primarySeries);
		console.log(secondarySeries);
		
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
		
		for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
			
			var first = track.trackpoints[0];
			
			// get the values
			var primary = primarySeries.provider(first, track, 0);
			var secondary = secondarySeries.provider(first, track, 0);
				
			graph.push([first.elapsed / 1000, primary, secondary, first.pos]);
			
			for (var i = 1; i < track.trackpoints.length; i+=5) {
				var trackpoint = track.trackpoints[i];
				
				// get the values
				primary = primarySeries.provider(trackpoint, track, i);
				secondary = secondarySeries.provider(trackpoint, track, i);
				
				graph.push([trackpoint.elapsed / 1000, primary, secondary, trackpoint.pos]);
			}
		}
		
		Trainings.activityView.setGraphData(graph, config);
	},
	
	selectionChanged: function(min, max) {
		var selection = { };
		selection.duration = formatDuration(max - min);
		this.set('selection', selection);
	}

});

Trainings.activityController = Trainings.ActivityController.create({
	view: function() {
		return Trainings.activityView;
	}
});

Trainings.activityView = Ember.View.create({
	marker: null,
	templateName: 'activity',
	seriesBinding: 'Trainings.activityController.series',
	activityBinding: 'Trainings.activityController.activity',
	summaryBinding: 'Trainings.activityController.summary',
	selectionBinding: 'Trainings.activityController.selection',
	primarySeriesBinding: 'Trainings.activityController.primarySeries',
	secondarySeriesBinding: 'Trainings.activityController.secondarySeries',
	
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
		
		this.graphDiv = document.getElementById("graph");
	},
	
	setMapData: function(mapData) {
		this.mapData = mapData;
		
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
	getMapData: function() {
		return this.mapData;
	},
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
	
	setGraphData: function(graphData, config) {
		if (this.graphDiv == null) {
			return;
		}
		
		var self = this;
		this.graphData = graphData;
		config = config || { };
		config.width = 770;
		config.height = 200;
		config.labelsDiv = "legend";
		config.highlightCircleSize = 6;
		config.interactionModel = Dygraph.Interaction.defaultModel;
		config.showRangeSelector = true;
		config.highlightCallback = function(event, x, points, row, seriesName) {
			var pos = graphData[row][3];
			if (pos !== undefined && pos !== null) {
				self.showPosition(pos);
			} else {
				self.showPosition(null);
			}
		};
		config.unhighlightCallback = function(e) {
			self.showPosition(null);
		};
		config.zoomCallback = function(min, max, yRanges) {
			Trainings.activityController.selectionChanged(min, max);
		};
		config.xLabelHeight = 12;
		config.colors = [ "red", "blue", "orange" ];
		config.fillAlpha = 0.3;
		config.axes = {
			x : {
				valueFormatter: formatDuration,
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
						result.push({ v: x, label: formatDuration(x, true) });
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
	}
});

Trainings.ActivityRepository = Ember.Object.extend({
	
	loadActivities: function(year, month, callback) {
		$.couch.db('trainings').view("app/overview", {
			success: function(data) {
				callback.success(data);
			},
			endkey: year + "-" + pad(month) + "-01 00:00:00",
			startkey: year + "-" + pad(month) + "-32 00:00:00",
			descending: true
		});
	},
	
	loadActivity: function(id, callback) {
		$.couch.db('trainings').openDoc(id, {
			success: function(data) {
				var activity = data.activity;
				callback.success(activity);
			},
			error: function(e) {
				console.log("ERROR");
				console.log(e);
			}
		});
	},
	
	
	loadEquipments: function(callback) {
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
								row.value.distance = formatDistance(distance);
							}
						});
						
						callback.success(data);
					}
				});
			}
		});
	}

});

Trainings.activityRepository = Trainings.ActivityRepository.create();
	
pad = function(num) {
	return num < 10 ? '0' + num : '' + num;
}

pad100 = function(num) {
	return num < 10 ? '00' + num : (num < 100 ? '0' + num : '' + num);
}

function formatDistance(distance) {
	var km = Math.floor(distance / 1000);
	var m = Math.floor(distance % 1000);
	return km + "." + pad100(m).substring(0, 1) + " km";
}

function formatDuration(duration, minimal) {
	var hours = Math.floor(duration / 3600);
	var minutes = Math.floor((duration / 60) % 60);
	var seconds = Math.floor(duration % 60);
	if (minimal && hours === 0) {
		return pad(minutes) + ":" + pad(seconds);
	} else if (minimal && hours < 10) {
		return hours + ":" + pad(minutes) + ":" + pad(seconds);
	}
	return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
}

function formatSpeed(speed) {
	var kmh = (speed * 3.6).toFixed(1);
	return kmh + " km/h";
}

function formatPace(speed) {
	var pace = (1 / speed) * 1000;
	var m = Math.floor(pace / 60);
	var s = Math.floor(pace % 60);
	return pad(m) + ":" + pad(s) + " min/km";
}

Trainings.locationHandler = function(hash) {
	var pagemap = {
		"#activities" : function(args) {
			if (Trainings.currentView !== Trainings.activityListView) {
				if (Trainings.currentView) {
					Trainings.containerView.get('childViews').removeAt(0);
				}
				Trainings.currentView = Trainings.activityListView;
				Trainings.containerView.get('childViews').pushObject(Trainings.currentView);
			}

			var now = new Date();
			var year = now.getFullYear();
			var month = now.getMonth() + 1;
			if (args && args.length > 0) {
				year = parseInt(args[0].substring(0, 4));
				month = parseInt(args[0].substring(5, 7));
			}
			Trainings.activityListController.showMonth(month, year);
		},
		"#activity" : function(args) {			
			if (Trainings.currentView) {
				Trainings.containerView.get('childViews').removeAt(0);
			}
			Trainings.currentView = Trainings.activityView;
			Trainings.containerView.get('childViews').pushObject(Trainings.currentView);

			var id = args[0];
			if (id) {
				Trainings.activityController.showActivity(id);
			}
		},
		"#equipments" : function(args) {
			if (Trainings.currentView !== Trainings.equipmentListView) {
				if (Trainings.currentView) {
					Trainings.containerView.get('childViews').removeAt(0);
				}
				Trainings.currentView = Trainings.equipmentListView;
				Trainings.containerView.get('childViews').pushObject(Trainings.currentView);
				Trainings.equipmentListController.loadEquipments();
			}
		}
	};

	var hash = window.location.hash;
	if (!hash) {
		hash = "#activities";
	}
	var splitted = hash.split(":");
	var page = splitted.shift();
	
	var controller = pagemap[page];
	if (controller) {
		controller(splitted);
	}
}

Trainings.containerView = Ember.ContainerView.create();
Trainings.containerView.appendTo("#main");

Ember.run(function() {		
	
	var hash = window.location.hash;
	Trainings.locationHandler(hash);
	
	window.onhashchange = function(e) {
		console.log("onhashchange: " + window.location.hash);
		Trainings.locationHandler(window.location.hash);
	};	
});
