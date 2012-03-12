function() {
  var date = $$(this).enddate;
  var year = date.getFullYear();
  var month = date.getMonth();
  
  $.log("month = " + month);
  if (month == 0) {
    year = year - 1;
    month = 12;	
  }
  var fmonth = month < 10 ? "0" + month : month;
  var startkey = year + "-" + fmonth + "-01";  
  var endkey = year + "-" + fmonth + "-32";
  $.log(startkey + " - " + endkey);
  $$(this).enddate = new Date(year, month - 1, 1);

  return {
    "view" : "overview",
    "descending" : true,
    "startkey" : endkey,
    "endkey" : startkey
  };
}