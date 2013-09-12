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
		
		// speed series
		series.pushObject({ 
			name:"speed", 
			index:index++,
			fillGraph:false,
			provider:function(curr, track, idx) { 
				var windowSize = 15;
				if (idx < windowSize || idx >= track.trackpoints.length - windowSize) {
					return null;
				}
				var prev = track.trackpoints.get(idx - windowSize);
				var next = track.trackpoints.get(idx + windowSize);
				var dist = next.distance - prev.distance;
				var time = next.elapsed - prev.elapsed;
				if (time == 0) {
					return null;
				}
				if (track.sport === 'CYCLING' || track.sport === 'MTB') {
					return dist / (time / 1000) * 3.6;
				} else {
					// s/m -> min / km --> x / 60 * 1000
					return (time / 1000) / dist * 1000 / 60;
				}
			}
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
		this.set('graphconfig', config);
		this.set('graphdata', graph);
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
