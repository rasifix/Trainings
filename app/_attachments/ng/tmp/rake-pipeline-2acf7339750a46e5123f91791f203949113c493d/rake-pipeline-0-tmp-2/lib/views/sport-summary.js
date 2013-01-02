
loader.register('trainings/views/sport-summary', function(require) {
require('trainings/core');

Trainings.SportSummaryView = Ember.View.extend({
    templateName: 'trainings/~templates/sport-summary'
});

});
