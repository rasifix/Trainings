
loader.register('trainings/controllers/activity-list', function(require) {
require('trainings/core');
require('trainings/models');

Trainings.ActivityListController = Ember.ArrayController.extend({
  content: []
});

});
