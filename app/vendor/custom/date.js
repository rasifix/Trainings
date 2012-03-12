/**
 * given a date in format yyyy-mm-dd HH:mm
 * function returns array [yyyy,mm,dd] where all components are converted to
 * numbers.
 */
function dateStringToArray(dateString) {
	var year = dateString.substring(0, 4);
	var month = dateString.substring(5, 7);
	var day = dateString.substring(8, 10);
	return [ parseInt(year, 10), parseInt(month, 10), parseInt(day, 10) ];
}
