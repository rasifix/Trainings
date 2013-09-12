function(doc) {
	if (doc.activity && doc.activity.summary) {
		var date = doc.activity.date; 
		var sport = doc.activity.summary.sport.toUpperCase();
		emit([date.substring(0,4),date.substring(5,7),date.substring(8,10)], sport);
	}
} 
