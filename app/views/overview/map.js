function(doc) { 
	if (doc.activity && doc.activity.summary) { 
		emit(doc.activity.date, doc.activity.summary);
	}
}