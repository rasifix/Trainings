
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
