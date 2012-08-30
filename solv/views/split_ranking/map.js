// split_ranking(year, event, fromControl, toControl, time) ->
// -- split ranking over all categories, sorted by time
// { name, yearOfBirth, city, club, from, to, time }
function(doc) {
	if (doc.type == 'orienteering.event' && doc.event) {
		var event = doc.event;
		for (var categoryName in event.categories) {
			var category = event.categories[categoryName];
			for (var i = 0; i < category.controls.length - 1; i++) {
				var ctrl1 = category.controls[i];
				var ctrl2 = category.controls[i + 1];
				
			}
		}
	}
	
}