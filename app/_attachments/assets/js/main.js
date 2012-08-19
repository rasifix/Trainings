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
	monthAndYearBinding: 'Trainings.activityListController.monthAndYear',
	activityCountBinding: 'Trainings.activityListController.content.length'
});


/*
 * Dashboard Controller + View
 */
Trainings.DashboardController = Ember.Object.extend({
	graphData: { },
	show: function() {
		var now = new Date();
		
		var endYear = now.getFullYear();
		var endWeek = now.getWeek();
		
		now.setDate(now.getDate() - 52 * 7);
		var startYear = now.getFullYear();
		var startWeek = now.getWeek();
		var that = this;
		Trainings.activityRepository.loadSummaryByWeek([startYear, startWeek], [endYear, endWeek + 1], {
			success: function(data) {
				var result = { };
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
					var year = date.getFullYear();
					if (date.getWeek() == 1 && date.getMonth() == 11) {
						year += 1;
					} else if (date.getWeek() >= 52 && date.getMonth() == 0) {
						year -= 1;
					}
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
				that.set('graphData', result);
			}
		});
	}
});

Trainings.dashboardController = Trainings.DashboardController.create();

Trainings.dashboardView = Ember.View.create({
	templateName: 'dashboard',
	graphDataBinding: 'Trainings.dashboardController.graphData',
	labels: [],
	
	didInsertElement: function() {
		// ignored for the moment
	},
	
	graphDataUpdated: function() {
		if (!this.get("element")) {
			console.log("too fast");
			return;
		}
		
		var that = this;
		var data = this.get("graphData");
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
		
		var records = [];
		var paper = new Raphael(div, 1000, 100);
		data.weeks.forEach(function(weekAndYear) {
			var week = data[weekAndYear[0]][weekAndYear[1]];
			var sum = sumrec(week);
			maxy = Math.max(maxy, sum);
			week.year = weekAndYear[0];
			records.push(week);
		});
				
		var width = 1000.0 / records.length;
		var height = 100;
		var colors = [ "#5F04B4", "#5FB404", "#FE642E", "#40A2FF" ];
				
		var time = function(wrec, sport) {
			var value = wrec[sport];
			if (value) {
				return value.totalTime;
			}
			return 0;
		}
		
		var rows = function(x, running, orienteering, cycling, mtb, record) {
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
					labels.push({ "label":"Running", "duration":formatDuration(record["RUNNING"].totalTime) });
				}
				if (record["ORIENTEERING"]) {
					total += record["ORIENTEERING"].totalTime;
					labels.push({ "label":"Orienteering", "duration":formatDuration(record["ORIENTEERING"].totalTime) });
				}
				if (record["CYCLING"]) {
					total += record["CYCLING"].totalTime;
					labels.push({ "label":"Cycling", "duration":formatDuration(record["CYCLING"].totalTime) });
				}
				if (record["MTB"]) {
					total += record["MTB"].totalTime;
					labels.push({ "label":"MTB", "duration":formatDuration(record["MTB"].totalTime) });
				}
				console.log(that);
				labels.push({ "label":"Total", "duration":formatDuration(total) });
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
			rows(x, running, orienteering, cycling, mtb, records[i]);
		}		
		
	}.observes("graphData")
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
		var result = { 
			paths: [], 
			lappoints: []
		};
		for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
				
			var path = [ ];

			var first = track.trackpoints[0];
			var hr = first.hr;
				
			var hrzone = hr === undefined ? null : hrToZone(hr);
			
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

				var newzone = hrToZone(trackpoint.hr);
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
		
		console.log("map data");
		console.log(result);
			
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
		
		var offset = 0;
		for (var trackIdx = 0; trackIdx < activity.tracks.length; trackIdx++) {
			var track = activity.tracks[trackIdx];	
			
			var first = track.trackpoints[0];
			
			// get the values
			var primary = primarySeries.provider(first, track, 0);
			var secondary = secondarySeries.provider(first, track, 0);
				
			graph.push([offset + first.elapsed / 1000, primary, secondary, first.pos]);
			
			for (var i = 1; i < track.trackpoints.length; i+=5) {
				var trackpoint = track.trackpoints[i];
				
				// get the values
				primary = primarySeries.provider(trackpoint, track, i);
				secondary = secondarySeries.provider(trackpoint, track, i);
				
				graph.push([offset + trackpoint.elapsed / 1000, primary, secondary, trackpoint.pos]);
			}
			
			offset += track.trackpoints[track.trackpoints.length - 1].elapsed / 1000;
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
		
		this.graphDiv = document.getElementById("graph");
	},
	
	setMapData: function(mapData) {
		this.mapData = mapData.paths;
		
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
	
	loadSummaryByWeek: function(start, end, callback) {
		$.couch.db('trainings').view("app/activitiesByWeek", {
			success: function(data) {
				callback.success(data);
			},
			startkey: start,
			endkey: end,
			group_level: 3
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
		"#dashboard" : function(args) {
			if (Trainings.currentView !== Trainings.dashboardView) {
				if (Trainings.currentView) {
					Trainings.containerView.get('childViews').removeAt(0);
				}
				Trainings.currentView = Trainings.dashboardView;
				Trainings.containerView.get('childViews').pushObject(Trainings.currentView);
			}
			Trainings.dashboardController.show();
		},
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
				var monthString = args[0].substring(4, 6);
				month = parseInt(monthString.charAt(0) === "0" ? monthString.substring(1) : monthString);
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
	console.log("go to page " + page);
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
