
loader.register('tests/trainings-tests', function(require) {
require('trainings/core');

module("trainings");

test("App is defined", function () {
  ok(typeof App !== 'undefined', "App is undefined");
});

});
