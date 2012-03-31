function(doc, req) {
  var content = { };
  content.data = [];
  content.config = { };
  var track = doc.activity.tracks[0];
  for (idx in track.trackpoints) {
    var trackpoint = track.trackpoints[idx];
    var row = [ trackpoint.elapsed / 1000, trackpoint.hr ];
    row.push(trackpoint.cadence !== undefined ? trackpoint.cadence : null);
    row.push(trackpoint.alt !== undefined ? trackpoint.alt : null);
    content.data.push(row);
  }
  
  content.config.labels = [ "elapsed", "hr", "cad", "alt" ];
  content.config.width = 800;
  content.config.height = 200;
  content.config.highlightCircleSize = 6;

  return {
    body : JSON.stringify(content),
	headers : {
      "Content-Type" : "application/json",
	}
  }
}