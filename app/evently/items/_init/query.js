function() {
  if ($$(this).enddate === undefined) {
  	$$(this).enddate = new Date();
  }  

  $.log($$(this).enddate);
  var date = $$(this).enddate;
  var month = date.getMonth() + 1;
  month = month < 10 ? "0" + month : month;
  var startkey = date.getFullYear() + "-" + month + "-01";  
  var endkey = date.getFullYear() + "-" + month + "-32";
  $.log(startkey + " - " + endkey);

  $.log($$);
  $.log($$(this));
  return {
    "view" : "overview",
    "descending" : "true",
    "startkey" : endkey,
    "endkey" : startkey
  };
}