(function($) {
	
	/*
	 *
	 */
	
	$.fn.slideshow = function(options) {
		var base = $(this);
		console.log("init")
		this.children().each(function(c) {
			console.log(c);
		});
		
		this.nextSlide = function() {
			
		}
		
		this.previousSlide = function() {
			
		}
		
		return this;						
	}
		
})(jQuery);

