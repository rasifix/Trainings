
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
