
loader.register('trainings/controllers/activities', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.ActivitiesController = Ember.ArrayController.extend({
  content: [],
  month: null,
  year: null,

	// calculated property to show month and year in title
	monthAndYear: function() {
	  var month = this.get('month');
	  var year = this.get('year');
	  return year + "-" + month;
  }.property('year', 'month'),
	
});

});
