function(doc) {
	if (doc.activity) {
		var date = doc.activity.date; 
		emit([date.substring(0,4),date.substring(5,7),date.substring(8,10)], doc.activity.tracks[0].sport);
	}
} 
