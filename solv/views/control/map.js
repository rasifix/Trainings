// control(year, event, control, time) ->
// -- athletes at control, sorted by time
// { control, name, yearOfBirth, city, club, time }
function(doc) {
	var intDiv = function(number, divisor) {
		return Math.floor(number / divisor);
	};
	var parseDayTime = function(value) {
		var arr = value.split(":");
		return parseInt(arr[0]) * 3600 + parseInt(arr[1]) * 60;
	};
	var parseTime = function(value) {
		var arr = value.split(":");
		var seconds = 0;
		if (arr.length == 3) {
			seconds += 3600 * parseInt(arr[0]);
			seconds += 60 * parseInt(arr[1]);
			seconds += parseInt(arr[2]);
		} else {
			seconds += 60 * parseInt(arr[0]);
			seconds += parseInt(arr[1]);
		}
		return seconds
	};
	var add = function(time, seconds) {
		var base = parseDayTime(time);
		return base + seconds;
	};
	var pad = function(value) {
		return value < 10 ? "0" + value : "" + value;
	};
	var formatTime = function(seconds) {
		if (seconds < 3600) {
			return intDiv(seconds, 60) + ":" + seconds % 60;
		} else {
			return intDiv(seconds, 3600) + ":" + pad(intDiv(seconds, 60) % 60) + ":" + pad(seconds % 60);
		}
	};
	var sumSplits = function(splits, endIdx) {
		var seconds = 0;
		for (var i = 0; i <= endIdx; i++) {
			var split = splits[i];
			if (split == null) {
				return null;
			}
			seconds += parseTime(split);
		}
		return seconds;
	};
	
	if (doc.type == 'orienteering.event' && doc.event) {
		var event = doc.event;
		var controls = { };
		for (var categoryName in event.categories) {
			var category = event.categories[categoryName];
			for (var controlIdx in category.controls) {
				var controlName = category.controls[controlIdx];
				if (controls[controlName] === undefined) {
					controls[controlName] = [ ];		
				}
				for (var athleteIdx in category.athletes) {
					var athlete = category.athletes[athleteIdx];
					if (athlete.splits[controlIdx] != null) {
						if (athlete.startTime == null) {
							continue;
						}
						var elapsed = sumSplits(athlete.splits, controlIdx);
						if (elapsed == null) {
							continue;
						}
						var time = add(athlete.startTime, elapsed);
						controls[controlName].push({
							name: athlete.name,
							year: athlete.year,
							city: athlete.city,
							club: athlete.club,
							control: controlName,
							category: categoryName,
							time: formatTime(time)
						});
					}
				}
			}
		}
		
		for (var controlIdx in controls) {
			var athletes = controls[controlIdx];
			for (var athletesIdx in athletes) {
				var athlete = athletes[athletesIdx];
				emit([event.year, event.title, athlete.control, athlete.time], athlete);
			}
		}
	}
}