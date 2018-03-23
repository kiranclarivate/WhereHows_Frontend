import Ember from 'ember';

export default Ember.Route.extend({
  // maintains backwards compatibility with legacy code
  // TODO: [DSS-6122] refactor so this may not be required
  controllerName: 'flows',

  setupController: function (controller, model) {
    this.controller.set('currentName', model.name);
  }
});
