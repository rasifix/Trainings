
loader.register('trainings/main', function(require) {
require('trainings/core');
require('trainings/router');
require('trainings/controllers');
require('trainings/views');

Trainings.initialize();

});
