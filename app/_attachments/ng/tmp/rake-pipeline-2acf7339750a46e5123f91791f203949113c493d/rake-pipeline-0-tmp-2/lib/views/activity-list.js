
loader.register('trainings/views/activity-list', function(require) {
require('trainings/core');

Trainings.ActivityListView = Ember.View.extend({
    templateName: 'trainings/~templates/activity-list'
});

});
