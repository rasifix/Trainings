function(doc) { 
	if (doc.activity) { 
	  var trackpoints = [];
		var activity = doc.activity;
		activity.tracks.forEach(function(track) {
		  track.trackpoints.forEach(function(trackpoint) {
		    if (trackpoint.pos) {
		      trackpoints.push(trackpoint.pos);
		    }
		  });
		});
		emit(doc._id, trackpoints);
	}
}