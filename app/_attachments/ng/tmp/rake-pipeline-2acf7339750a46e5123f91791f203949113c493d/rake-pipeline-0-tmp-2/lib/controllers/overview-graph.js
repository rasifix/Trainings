
loader.register('trainings/controllers/overview-graph', function(require) {
require('trainings/core');

Trainings.OverviewGraphController = Ember.Controller.extend({
  weekClicked: function(week, year) {
    var endDate = new Date(year, 0, 1);
    endDate.setDate(endDate.getDate() - endDate.getDay() + 1 + (week - 1) * 7);
    var startDate = new Date(endDate);
    startDate.setDate(startDate.getDate() + 7);
    Trainings.router.activityListController.set('content', Trainings.ActivityOverview.find({
      endDate: endDate,
      startDate: startDate,
      descending: true
    }));
  }
});

});
