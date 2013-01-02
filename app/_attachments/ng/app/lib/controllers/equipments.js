require('trainings/core');
require('trainings/models');

/*
 * Equipments Controller
 */
Trainings.EquipmentsController = Ember.ArrayProxy.extend({
  content: [],
  loading: true
});
