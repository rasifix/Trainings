/**
 * jQuery bxSlider v3.0
 * http://bxslider.com
 *
 * Copyright 2011, Steven Wanderski
 * http://bxcreative.com
 *
 * Free to use and abuse under the MIT license.
 * http://www.opensource.org/licenses/mit-license.php
 * 
 */


(function($) {
	
	$.fn.bxSlider = function(options) {			
		var defaults = {
			speed: 500,													// integer - in ms, duration of time slide transitions will occupy
			easing: 'swing',                    // used with jquery.easing.1.3.js - see http://gsgd.co.uk/sandbox/jquery/easing/ for available options
			wrapperClass: 'bx-wrapper',					// string - classname attached to the slider wraper
			randomize: false,
			onBeforeSlide: function() {},				// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
			onAfterSlide: function() {},					// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
			onLastSlide: function() {},					// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
			onFirstSlide: function() {},					// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
			onNextSlide: function() {},					// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
			onPrevSlide: function() {},					// function(currentSlideNumber, totalSlideQty, currentSlideHtmlObject) - advanced use only! see the tutorial here: http://bxslider.com/custom-pager
		}
		
		var options = $.extend(defaults, options);
		
		// cache the base element
		var base = this;
		// initialize (and localize) all variables
		var $parent = '';
		var $origElement = '';
		var $children = '';
		var $outerWrapper = '';
		var $firstChild = '';
		var childrenWidth = '';
		var childrenOuterWidth = '';
		var wrapperWidth = '';
		var wrapperHeight = '';
		var $pager = '';	
		var interval = '';
		var $stopHtml = '';
		var $startContent = '';
		var $stopContent = '';
		var loaded = false;
		var childrenMaxWidth = 0;
		var childrenMaxHeight = 0;
		var currentSlide = 0;	
		var origLeft = 0;
		var origTop = 0;
		var origShowWidth = 0;
		var origShowHeight = 0;
		var isWorking = false;
    
		var firstSlide = 0;
		var lastSlide = $children.length - 1;
		
						
		// PUBLIC FUNCTIONS
						
		this.goToSlide = function(number){
			if (!isWorking) {
				isWorking = true;
				// set current slide to argument
				currentSlide = number;
				options.onBeforeSlide(currentSlide, $children.length, $children.eq(currentSlide));

				slide = number;
				// check for first slide callback
				if (slide == firstSlide){
					options.onFirstSlide(currentSlide, $children.length, $children.eq(currentSlide));
				}
				// check for last slide callback
				if (slide == lastSlide){
					options.onLastSlide(currentSlide, $children.length, $children.eq(currentSlide));
				}

				$parent.animate({'left': '-'+getSlidePosition(slide, 'left')+'px'}, options.speed, options.easing, function() {
					isWorking = false;
					// perform the callback function
					options.onAfterSlide(currentSlide, $children.length, $children.eq(currentSlide));
				});
			}
		}
		
		this.goToNextSlide = function() {
			if (!isWorking){
				var slideLoop = false;
				// make current slide the old value plus moveSlideQty
				currentSlide += 1;
				// if current slide has looped on itself
				if (currentSlide <= lastSlide){
					// next slide callback
					options.onNextSlide(currentSlide, $children.length, $children.eq(currentSlide));
					// move to appropriate slide
					base.goToSlide(currentSlide);						
				} else {
					currentSlide -= 1;
				}
			} // end if(!isWorking)		
		} // end function
		
		this.goToPreviousSlide = function(){
			if (!isWorking){
				var slideLoop = false;
				currentSlide -= 1;
				// if current slide has looped on itself
				if (currentSlide < 0) {
					currentSlide = 0;
				}
				// next slide callback
				options.onPrevSlide(currentSlide, $children.length, $children.eq(currentSlide));
				// move to appropriate slide
				base.goToSlide(currentSlide);
			}
		} // end function
		
		this.goToFirstSlide = function() {
			base.goToSlide(firstSlide);
		}
		
		this.goToLastSlide = function() {
			base.goToSlide(lastSlide);
		}
		
		this.getCurrentSlide = function() {
			return currentSlide;
		}
		
		this.getSlideCount = function(){
			return $children.length;
		}
				
		/**
		 * Initialize a new slideshow
		 */		
		this.initShow = function() {			
			// reinitialize all variables
			// base = this;
			$parent = $(this);
			$origElement = $parent.clone();
			$children = $parent.children();
			$outerWrapper = '';
			$firstChild = $parent.children(':first');
			childrenWidth = $firstChild.width();
			childrenMaxWidth = 0;
			childrenOuterWidth = $firstChild.outerWidth();
			childrenMaxHeight = 0;
			wrapperWidth = getWrapperWidth();
			wrapperHeight = getWrapperHeight();
			isWorking = false;
			$pager = '';	
			currentSlide = 0;	
			origLeft = 0;
			origTop = 0;
			interval = '';
			$stopHtml = '';
			$startContent = '';
			$stopContent = '';
			loaded = false;
			origShowWidth = 0;
			origShowHeight = 0;
      
			firstSlide = 0;
			lastSlide = $children.length - 1;
						
			// get the largest child's height and width
			$children.each(function(index) {
				if ($(this).outerHeight() > childrenMaxHeight) {
					childrenMaxHeight = $(this).outerHeight();
				}
				if ($(this).outerWidth() > childrenMaxWidth) {
					childrenMaxWidth = $(this).outerWidth();
				}
			});
			
			if (options.randomize) {
				Array.prototype.shuffle = function() {
					var i = this.length;
					while (--i > 0) {
						var j = Math.floor(Math.random() * (i + 1));
						var tempi = this[i];
						var tempj = this[j];
						this[i] = tempj;
						this[j] = tempi;
					}
					return this;
				};
				
				var arr = [];
				$children.each(function(index) {
					arr.push(this);
					$(this).detach();
				});
				arr.shuffle();
				arr.forEach(function(el) {
					$(el).appendTo($parent);
				});
			}

			currentSlide = 0;
			origLeft = 0;
			origTop = 0;
						
			// set initial css
			initCss();

			// show captions
			if (options.captions){
				showCaptions();
			}
			// perform the callback function
			options.onAfterSlide(currentSlide, $children.length, $children.eq(currentSlide));
		}
		
		/**
		 * Destroy the current slideshow
		 */		
		this.destroyShow = function(){			
			// remove any elements that have been appended
			$('.bx-pager', $outerWrapper).remove();
			// unwrap all bx-wrappers
			$parent.unwrap().unwrap().removeAttr('style');
			// remove any styles that were appended
			$parent.children().removeAttr('style').not('.pager').remove();
			// remove any children that were appended
			$children.removeClass('pager');
			
		}
		
		/**
		 * Reload the current slideshow
		 */		
		this.reloadShow = function(){
			base.destroyShow();
			base.initShow();
		}
		
		// PRIVATE FUNCTIONS
		
		/**
		 * Creates all neccessary styling for the slideshow
		 */		
		function initCss(){
			// CSS for horizontal mode
			// wrap the <ul> in div that acts as a window and make the <ul> uber wide
			$parent
			.wrap('<div class="'+options.wrapperClass+'" style="width:'+wrapperWidth+'px; position:relative;"></div>')
			.wrap('<div class="bx-window" style="position:relative; overflow:hidden; width:'+wrapperWidth+'px;"></div>')
			.css({
			  width: '999999px',
			  position: 'relative',
				left: '-'+(origLeft)+'px'
			});
			$parent.children().css({
				width: childrenWidth,
			  'float': 'left',
			  listStyle: 'none'
			});					
			$outerWrapper = $parent.parent().parent();
			$children.addClass('pager');
		}
		
		/**
		 * Returns the left offset of the slide from the parent container
		 */		
		function getSlidePosition(number, side){			
			if (side == 'left') {
				var position = $('.pager', $outerWrapper).eq(number).position().left;
			} else if (side == 'top') {
				var position = $('.pager', $outerWrapper).eq(number).position().top;
			}
			return position;
		}
		
		/**
		 * Returns the width of the wrapper
		 */		
		function getWrapperWidth(){
			var wrapperWidth = $firstChild.outerWidth();
			return wrapperWidth;
		}
		
		/**
		 * Returns the height of the wrapper
		 */		
		function getWrapperHeight(){
			// if displaying multiple slides, multiple wrapper width by number of slides to display
			var wrapperHeight = $firstChild.outerHeight();
			return wrapperHeight;
		}
												
		this.each(function() {
			// make sure the element has children
			if ($(this).children().length > 0) {
				base.initShow();
			}
		});
				
		return this;						
	}
	
	jQuery.fx.prototype.cur = function() {
		if (this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null)) {
			return this.elem[ this.prop ];
		}

		var r = parseFloat(jQuery.css(this.elem, this.prop));
		// return r && r > -10000 ? r : 0;
		return r;
	}
		
})(jQuery);

