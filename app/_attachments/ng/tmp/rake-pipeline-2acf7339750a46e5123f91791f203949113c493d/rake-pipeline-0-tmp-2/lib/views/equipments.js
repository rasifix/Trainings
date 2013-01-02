
loader.register('trainings/views/equipments', function(require) {
require('trainings/core');

Trainings.EquipmentsView = Ember.View.extend({
    templateName: 'trainings/~templates/equipments'
});

});
