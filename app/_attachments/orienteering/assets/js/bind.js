$(document).one('pageinit', function(e) {
	$("#slider").ready(function() {
	    var slider = $('#slider').bxSlider({ captions: false, controls: false });
		slider.on("swipeleft", function() {
			slider.goToNextSlide();
		});

		slider.on("swiperight", function() {
			slider.goToPreviousSlide();
		});
	});	
});