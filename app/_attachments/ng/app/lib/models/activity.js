require('trainings/core');

Trainings.Activity = Ember.Object.extend({
  id: null,
  sport: null,
  duration: null,
  distance: null,
  hr: null,
  tracks: Ember.ArrayProxy.extend({ content:[] })
});

Trainings.Activity.reopenClass({
	find: function(id) {
	  var result = Trainings.Activity.create();
	  result.id = id;
		$.couch.db('trainings').openDoc(id, {
			success: function(doc) {
			  var activity = doc.activity;
			  result.set('date', activity.date);
			  result.set('sport', activity.sport);
			  result.set('duration', Trainings.formatDuration(activity.summary.totalTime));
			  result.set('distance', Trainings.formatDistance(activity.summary.distance));
			  result.set('hr', activity.summary.hr);
			  result.set('summary', activity.summary);
			  result.set('tracks', activity.tracks);
			  console.log(result);
      },
      error: function(e) {
        console.log("sorry pal, you have a bad day");
      }
    });
    return result;
	}
});
