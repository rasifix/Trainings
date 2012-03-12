function(doc) {
	// !code vendor/custom/date.js
	if (!doc.activity) {
		return;
	}
	var date = dateStringToArray(doc.activity.date);
	if (doc.activity.sport) {
		emit([doc.activity.sport, date[0], date[1], date[2]], doc.activity.summary);
	}
}
