
loader.register('trainings/controllers/nav', function(require) {
require('trainings/core');

Trainings.NavController = Ember.Controller.extend({
  selected: null
});

});
