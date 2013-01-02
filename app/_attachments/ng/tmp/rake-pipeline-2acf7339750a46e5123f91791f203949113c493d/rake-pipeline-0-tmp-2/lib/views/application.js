
loader.register('trainings/views/application', function(require) {
require('trainings/core');

Trainings.ApplicationView = Ember.View.extend({
  templateName: 'trainings/~templates/application'
});

});
