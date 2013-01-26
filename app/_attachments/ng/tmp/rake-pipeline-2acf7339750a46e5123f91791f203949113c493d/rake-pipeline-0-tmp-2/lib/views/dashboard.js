
loader.register('trainings/views/dashboard', function(require) {
require('trainings/core');

Trainings.DashboardView = Ember.View.extend({
	templateName: 'trainings/~templates/dashboard'
});

});
