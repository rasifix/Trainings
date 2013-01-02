
loader.register('trainings/views/activities', function(require) {
require('trainings/core');

Trainings.ActivitiesView = Ember.View.extend({
    templateName: 'trainings/~templates/activities'
});

});
