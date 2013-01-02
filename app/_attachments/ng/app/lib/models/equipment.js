require('trainings/core');

Trainings.Equipment = Ember.Object.extend({ });

Trainings.Equipment.reopenClass({
	find: function() {
    var result = [];
    
		$.couch.db('trainings').view("app/equipments", {
			success: function(data) {
				$.couch.db('trainings').view("app/activitiesByEquipment", {
					reduce: true,
					group_level: 1,
					success: function(summaries) {
						var map = { };
						summaries.rows.forEach(function(row) {
							map[row.key[0]] = row.value.distance;
						});
						
						data.rows.forEach(function(row) {
							var distance = map[row.id];
							if (distance) {
								row.value.distance = Trainings.formatDistance(distance);
							}
							result.pushObject(Trainings.Equipment.create({
  							id: row.id,
  							name: row.value.name,
  							brand: row.value.brand,
  							dateOfPurchase: row.value.dateOfPurchase.substring(0, 10),
  							distance: row.value.distance
  						}));
						});
					}
				});
			}
		});
		
    return result;
	}
});
