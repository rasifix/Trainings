function(doc) {
	// !code vendor/custom/date.js
	if (!doc.activity) {
		return;
	}
	var date = dateStringToArray(doc.activity.date);
	if (doc.activity.equipments && doc.activity.equipments.length > 0) {
		var equipments = doc.activity.equipments;
		var obj = { };
		for (idx in equipments) {
			obj[equipments[idx].id] = true;
		}
		for (id in obj) {
			emit([id, date[0], date[1], date[2]], doc.activity.summary);
		}
	} else {
		emit([null, date[0], date[1], date[2]], doc.activity.summary);
	}
}
