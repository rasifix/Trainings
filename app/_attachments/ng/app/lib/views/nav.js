require('trainings/core');

Trainings.NavView = Ember.View.extend({
  templateName: 'trainings/~templates/nav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),
    isActive: function() {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected')
  })
});