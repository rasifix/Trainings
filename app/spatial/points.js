function(doc) {
	if (doc.activity) {
		for (var trackIdx in doc.activity.tracks) {
			var track = doc.activity.tracks[trackIdx];
			for (var tpIdx in track.trackpoints) {
				var tp = track.trackpoints[tpIdx];
				if (tp.pos) {
					emit({ type: "Point", coordinates: [tp.pos.lat, tp.pos.lng] }, [doc._id, null]);
				}
			}
		}
	}
}
