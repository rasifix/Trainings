// list function providing condensed list of grouped activities per week per sport
// to be used with view 'activitiesByWeek' (or any other view that provides the following key)
//
// [year,week,sport]
//
// and value
// 
// {
//   'totalTime' : 4299,
//   'distance'  : 12345
// }
//
function(doc, req) {   
  provides("json", function() {
    send("[");
    
    var pending = false;
    
    while (row = getRow()) {
      if (pending) {
        send(',\n');
      }
      send('{"year":' 
              + row.key[0] 
              + ',"week":' 
              + row.key[1]
              + ',"sport":'
              + '"' + row.key[2] + '"'
              + ',"totalTime":'
              + row.value.totalTime
              + ',"distance":'
              + row.value.distance
              + '}');
      pending = true;
    }   
    return "]";
  }); 
}