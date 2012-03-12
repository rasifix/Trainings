function(doc) {
	if (doc.type == 'equipment') {
		emit(doc.equipment.name, doc.equipment);
	}
}
