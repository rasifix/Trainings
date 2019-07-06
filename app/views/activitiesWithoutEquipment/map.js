function(doc) {
	if (doc.activity) {
	  if (!doc.activity.equipments || doc.activity.equipments.length === 0) {
  		emit(doc.activity.date, {
  		  sport: doc.activity.sport
  		});
	  }
	}
} 
