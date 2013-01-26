require('trainings/core');

Trainings.Router = Ember.Router.extend({
  enableLogging: true,
  
  root: Ember.Route.extend({
    gotoDashboard: Ember.Route.transitionTo('root.index'),
    gotoActivities: Ember.Route.transitionTo('root.activities.index'),
    gotoEquipments: Ember.Route.transitionTo('root.equipments'),

    showActivity: Ember.Route.transitionTo('activity'),	
    
    index: Ember.Route.extend({
      route: '/',
      connectOutlets: function(router, context) {
        router.set('navController.selected', 'dashboard');
        router.get('applicationController').connectOutlet('dashboard');
        
        var now = new Date();
    		var endYear = now.getYearOfWeek();
    		var endWeek = now.getWeek();
    		now.setDate(now.getDate() - 52 * 7);
    		var startYear = now.getYearOfWeek();
    		var startWeek = now.getWeek();
    		
        router.get('dashboardController').connectOutlet({
          outletName: 'overview',
          viewClass: Trainings.OverviewGraphView,
          controller: Trainings.OverviewGraphController.create(),
          context: Trainings.ActivitySummary.summaryByWeek([startYear, startWeek], [endYear, endWeek + 1])
        });
        router.get('dashboardController').connectOutlet({
          outletName: 'activities',
          viewClass: Trainings.ActivityListView,
          controller: Trainings.router.activityListController,
          context: Trainings.ActivityOverview.query({ limit:5, descending:true })
        });
        router.get('dashboardController').connectOutlet({
          outletName: 'sportsummary',
          viewClass: Trainings.SportSummaryView,
          controller: Trainings.SportSummaryController.create(),
          context: Trainings.SportSummary.find()
        });
      }
    }),
    
    activities: Ember.Route.extend({
      route: '/activities',
      index: Ember.Route.extend({
        route: '/',
        connectOutlets: function(router, context) {
          console.log(context);
          var date = new Date();
          router.transitionTo('root.activities.show', { year:date.getFullYear(), month:date.getMonth() + 1 });
        }
      }),
      show: Ember.Route.extend({
		    route: '/:year/:month',
		    connectOutlets: function(router, context) {
          router.set('navController.selected', 'activities');
		      router.get('activitiesController').set('month', parseInt(context.month));
		      router.get('activitiesController').set('year', parseInt(context.year));
		      router.get('activitiesController').connectOutlet({
		        viewClass: Trainings.ActivityListView
		      });
          router.get('applicationController').connectOutlet('activities', Trainings.ActivityOverview.find(context));
		    },
		    		    
		    previous: function(router, context) {
          var newMonth = router.get('activitiesController.month') - 1;
          var newYear = router.get('activitiesController.year');
      		if (newMonth == 0) {
      			newMonth = 12;
      			newYear -= 1;
      		}
      		router.transitionTo("root.activities.show", { month: newMonth, year : newYear });
      	},

      	next: function(router) {
          var newMonth = router.get('activitiesController.month') + 1;
          var newYear = router.get('activitiesController.year');
      		if (newMonth == 13) {
      			newMonth = 1;
      			newYear += 1;
      		}
      		router.transitionTo("root.activities.show", { month: newMonth, year : newYear });
      	}
	    })
    }),
    
    activity: Ember.Route.extend({
      route: "/activity/:id",
      deserialize: function(router, context) {
        console.log("deserialize");
        console.log(context);
        return Trainings.Activity.find(context.id);
      },
      serialize: function(router, context) {
        console.log("serialize activity");
        console.log(context);
        console.log(context.id);
        return {
          id: context.id
        }
      },
      connectOutlets: function(router, activity) {
        console.log("---->");
        console.log(activity);
        router.set('navController.selected', 'activities');
        router.get('applicationController').connectOutlet('activity', Trainings.Activity.find(activity.id));
      }
    }),
    
    equipments: Ember.Route.extend({
      route: '/equipments',
      connectOutlets: function(router, context) {
        router.set('navController.selected', 'equipments');
        router.get('applicationController').connectOutlet('equipments', Trainings.Equipment.find());
      }
    })
  })
});
