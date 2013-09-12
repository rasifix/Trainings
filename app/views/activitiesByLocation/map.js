function(doc) { 
	if (doc.activity && doc.activity.summary && doc.activity.summary.places) { 
	  for (var idx in doc.activity.summary.places) {
		  emit([doc.activity.summary.places[idx], doc.activity.summary.sport], doc.activity.summary);
	  }
	}
}