
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
