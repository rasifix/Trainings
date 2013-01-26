// list function providing icalendar of all activities
// to be used with view 'activitylist' (or any other view that provides the following value)
//
// { 
//   startDate:'20130103T165300',
//   endDate:'20130103T172413',
//   sport:'RUNNING'
// }
//
function(doc, req) {   
  provides("ics", function() {
    start({ "headers" : {"Content-type" : "text/calendar"}});
    
    var buf = "BEGIN:VCALENDAR\n";
    buf += "PRODID:-//Trainings Calendar\n";
    buf += "VERSION:2.0\n";
    buf += "METHOD:PUBLISH\n";
    buf += "CALSCALE:GREGORIAN\n";
    buf += "X-WR-CALNAME:Trainings Calendar\n";
    send(buf);
    while (row = getRow()) {
      var buf = "";      
      buf += "BEGIN:VEVENT\n";
      buf += "DTSTART;VALUE=DATE:" + row.value.startDate + "\n";
      buf += "DTEND;VALUE=DATE:" + row.value.endDate + "\n";
      buf += "DTSTAMP:" + row.value.startDate + "\n";
      buf += "SUMMARY:" + row.value.sport + "\n";
      //buf += "DESCRIPTION:\n";
      //buf += "LOCATION:" + row.value.location + "\n";
      buf += "CATEGORIES:trainings\n";
      buf += "CLASS:PUBLIC\n";
      buf += "STATUS:CONFIRMED\n";
      buf += "TRANSP:TRANSPARENT\n";
      //buf += "URL:\n";
      buf += "END:VEVENT\n";
      send(buf);
    }   
    
    return  "END:VCALENDAR";
  }); 
}