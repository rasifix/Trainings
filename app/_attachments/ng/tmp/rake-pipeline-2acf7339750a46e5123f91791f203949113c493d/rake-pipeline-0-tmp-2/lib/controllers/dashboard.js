
loader.register('trainings/controllers/dashboard', function(require) {
require('trainings/core');

Trainings.DashboardController = Ember.Controller.extend({
	graphData: { },
	show: function() {
		var now = new Date();
		
		var endYear = now.getYearOfWeek();
		var endWeek = now.getWeek();
		
		now.setDate(now.getDate() - 52 * 7);
		var startYear = now.getYearOfWeek();
		var startWeek = now.getWeek();

		this.set('graphData', Trainings.ActivitySummary.summaryByWeek([startYear, startWeek], [endYear, endWeek + 1]));
	}
});

});
