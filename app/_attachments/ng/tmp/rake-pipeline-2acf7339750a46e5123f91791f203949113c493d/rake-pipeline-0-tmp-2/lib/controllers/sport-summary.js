
loader.register('trainings/controllers/sport-summary', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.SportSummaryController = Ember.ArrayController.extend({
  content: []
});

});
