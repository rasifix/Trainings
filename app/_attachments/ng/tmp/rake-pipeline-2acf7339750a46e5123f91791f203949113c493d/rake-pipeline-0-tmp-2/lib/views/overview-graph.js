
loader.register('trainings/views/overview-graph', function(require) {
require('trainings/core');

Trainings.OverviewGraphView = Ember.View.extend({
  templateName: 'trainings/~templates/overview-graph',
  labels: [],
    
  graphDataUpdated: function() {
		if (!this.get("element")) {
			console.log("too fast");
			return;
		}

		if (!this.get('controller.content.loaded')) {
		  console.log("graph data not yet loaded");
		  return;
		}

		var that = this;
		var data = this.get("controller.content");
		var div = document.getElementById("dashboard");
		
		var bardiv = document.createElement("div");
		var scrolldiv = document.createElement("div");
		
		div.appendChild(bardiv);
		div.appendChild(scrolldiv);
		
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
		console.log(data);

    // constants
		var paperWidth = 940;
		var paperHeight = 100;
		var barWidth = 18;
    var viewportWidth = paperWidth;
    var viewportHeight = 100;
		
		var records = [];
		data.weeks.forEach(function(weekAndYear) {
			var week = data[weekAndYear[0]][weekAndYear[1]];
			var sum = sumrec(week);
			maxy = Math.max(maxy, sum);
			week.year = weekAndYear[0];
			records.push(week);
		});
		
		// transform from svg canvas x to model idx
		var viewToModel = function(x) {
		  return Math.ceil(x / barWidth);
		};
		
		// transform from model idx + svg canvas x
		var modelToView = function(idx) {
		  return idx * barWidth;
		};
		
		// drag the baby
		$(bardiv).on("mousedown", function(e) {
		  e.preventDefault();
		  
		  var startx = e.clientX;
		  var initialViewBoxX = paper.canvas.viewBox.baseVal.x;
		  var mousemove = function(e) {
		    var dx = e.clientX - startx;
		    paper.setViewBox(-dx + initialViewBoxX, 0, viewportWidth, viewportHeight, false);
		    console.log(dx + " : " + paper.canvas.viewBox.baseVal.x);
  		};
  		var mouseup = function(e) {
		    $(document).off("mousemove", mousemove);
		    $(document).off("mouseup", mouseup);
		    var dx = e.clientX - startx;
		    paper.setViewBox(-dx + initialViewBoxX, 0, viewportWidth, viewportHeight, false);
		  };
		  $(document).on("mousemove", mousemove);
		  $(document).on("mouseup", mouseup);
		});
		
		// start overview diff
		var scroller = new Raphael(scrolldiv, paperWidth, 30);
		for (var y = 2008; y <= 2013; y++) {
		  var width = paperWidth / (2013 - 2008 + 1);
		  var r = scroller.rect((y - 2008) * width, 0, (y - 2008 + 1) * width, 30);
		  r.attr({fill:"#e8e8e8", stroke:"none"});
		  r.data("year", y);
		  r.click(function(e) { console.log(this.data("year")); });
		  
		  scroller.text((y - 2008) * width + width / 2, 15, "" + y);
		  
		  if (y < 2013) {
		    scroller.path("M" + (y - 2008 + 1) * width + ",0 L" + (y - 2008 + 1) * width + ",30");
	    }
		}
		scroller.rect((2013 - 2008) * width, 0, (2013 - 2008 + 1) * width, 30).attr({fill:"orange",opacity:0.5,stroke:"none"});
    
		var paper = new Raphael(bardiv, paperWidth, paperHeight);
		paper.setViewBox(-viewportWidth, 0, viewportWidth, viewportHeight);
		
		window.paper = paper;
		
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
			var rect = paper.rect(x, y, barWidth - 1, running / maxy * viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[0]);
			rect.attr("stroke", "none");

			y += running / maxy * viewportHeight;
			rect = paper.rect(x, y, barWidth - 1, orienteering / maxy * viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[1]);
			rect.attr("stroke", "none");

			y += orienteering / maxy * viewportHeight;
			rect = paper.rect(x, y, barWidth - 1, cycling / maxy * viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[2]);
			rect.attr("stroke", "none");

			y += cycling / maxy * viewportHeight;
			rect = paper.rect(x, y, barWidth - 1, mtb / maxy * viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[3]);
			rect.attr("stroke", "none");

			y += mtb / maxy * viewportHeight;
			rect = paper.rect(x, y, barWidth - 1, mtbOrienteering / maxy * viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", colors[4]);
			rect.attr("stroke", "none");

			rect = paper.rect(x, 0, barWidth, viewportHeight);
			rect.translate(0, viewportHeight);
			rect.scale(1, -1, 0, 0);
			rect.attr("fill", "white");
			rect.attr("stroke", "none");
			rect.node.setAttributeNS(null, "class", "screen");	
			rect.click(function(e) {
			  that.get('controller').weekClicked(record.week, record.year);
			});

			rect.hover(function(e) {
				var total = 0;
				var labels = [];
				labels.push({ "label":"Week", "duration":record.week + "-" + record.year });
				if (record["RUNNING"]) {
					total += record["RUNNING"].totalTime;
					labels.push({ "label":"Running", "duration":Trainings.formatDuration(record["RUNNING"].totalTime), "style":"running" });
				}
				if (record["ORIENTEERING"]) {
					total += record["ORIENTEERING"].totalTime;
					labels.push({ "label":"Orienteering", "duration":Trainings.formatDuration(record["ORIENTEERING"].totalTime), "style":"orienteering" });
				}
				if (record["CYCLING"]) {
					total += record["CYCLING"].totalTime;
					labels.push({ "label":"Cycling", "duration":Trainings.formatDuration(record["CYCLING"].totalTime), "style":"cycling" });
				}
				if (record["MTB"]) {
					total += record["MTB"].totalTime;
					labels.push({ "label":"MTB", "duration":Trainings.formatDuration(record["MTB"].totalTime), "style":"mtb" });
				}
				if (record["MTB-ORIENTEERING"]) {
					total += record["MTB-ORIENTEERING"].totalTime;
					labels.push({ "label":"Bike-OL", "duration":Trainings.formatDuration(record["MTB-ORIENTEERING"].totalTime), "style":"mtb-orienteering" });
				}
				labels.push({ "label":"Total", "duration":Trainings.formatDuration(total) });
				that.set('labels', labels);

			}, function(e) {
				that.set('labels', [ ]);
			});
		};

		for (var i = records.length - 1; i >= 0; i--) {
			var x = -(records.length - i) * barWidth;
			var running = time(records[i], "RUNNING");
			var cycling = time(records[i], "CYCLING");
			var orienteering = time(records[i], "ORIENTEERING");
			var mtb = time(records[i], "MTB");
			var mtbOrienteering = time(records[i], "MTB-ORIENTEERING");
			rows(x, running, orienteering, cycling, mtb, mtbOrienteering, records[i]);
		}		
	}.observes("controller.content.loaded")
	
});

});
