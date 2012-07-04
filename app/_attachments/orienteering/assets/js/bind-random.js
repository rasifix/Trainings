$(document).one('pageinit', function(e) {
	var currentSlide = null;
	
	$("#slider").ready(function() {
	    var slider = $('#slider').bxSlider({ 
			captions: false, 
			controls: false, 
			randomize: true,
			onAfterSlide: function() {
				if (currentSlide != null) {
					currentSlide.removeClass("flip");
					currentSlide = null;
				}
			}
		});
		slider.on("swipeleft", function() {
			slider.goToNextSlide();
		});

		slider.on("swiperight", function() {
			slider.goToPreviousSlide();
		});
	});	
	
	// tap handler - jquery mobiles tap is fundamentally broken...
	$(".cont").on("mousedown", function(e) {
		var x = e.clientX;
		var y = e.clientY;
		var target = e.currentTarget;
		var uphandler = function(e) {
			if (e.clientX == x && e.clientY == y) {
				currentSlide = $(target).children(".slide");
				if (currentSlide.hasClass("flip")) {
			    	currentSlide.removeClass("flip");
				} else {
			    	currentSlide.addClass("flip");
				}
			}
			$(document).unbind("mouseup", uphandler);
		};
		$(document).on("mouseup", uphandler);
	});	
});
