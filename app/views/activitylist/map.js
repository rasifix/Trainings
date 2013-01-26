function(doc) {
  var pad2 = function(number) {
    if (number < 10) {
      return '0' + number;
    } else {
      return '' + number;
    }
  };
  
  var formatDate = function(date) {
    return date.getFullYear() + pad2(date.getMonth() + 1) + pad2(date.getDate()) + 'T' + pad2(date.getHours()) + pad2(date.getMinutes()) + pad2(date.getSeconds());
  };
  
	if (doc.activity) {
	  // 2013-01-04 13:35:54
		var date = doc.activity.date; 
		
		var year = parseInt(date.substring(0, 4));
		var month = parseInt(date.substring(5, 7)) - 1;
		var day = parseInt(date.substring(8, 10));
		var hour = parseInt(date.substring(11, 13));
		var minute = parseInt(date.substring(14, 16));
		var second = parseInt(date.substring(17, 19));
		var startDate = new Date(year, month, day, hour, minute, second);
				
		var endDate = new Date(startDate);
		endDate.setSeconds(endDate.getSeconds() + parseInt(doc.activity.summary.totalTime));
		
		var value = {
		  'startDate' : formatDate(startDate),
		  'endDate' : formatDate(endDate),
		  'sport' : doc.activity.sport,
		  'location' : doc.activity.summary.places[0]
		};
		emit(date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10) + date.substring(11, 13) + date.substring(14, 16) + date.substring(17, 19), value);
	}
} 
