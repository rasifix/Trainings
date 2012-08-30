// ranking(year, event, category, totalTime) ->
// standard ranking for a category, sorted by totalTime
function(doc) {
	if (doc.type == "orienteering.event") {
		for (var categoryName in doc.event.categories) {
			var category = doc.event.categories[categoryName];
			for (var athletesIdx in category.athletes) {
				var athlete = category.athletes[athletesIdx];
				emit([doc.event.year, doc.event.title, categoryName, athlete.totalTime], athlete);
			}
		}
	}
}
