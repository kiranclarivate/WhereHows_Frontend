import Ember from 'ember';

const { Component, get } = Ember;

export default Component.extend({
  didInsertElement() {
    this._super(...arguments);
    const metric = get(this, 'model');

    if (metric) {
      self.initializeXEditable(
        metric.id,
        metric.description,
        metric.dashboardName,
        metric.sourceType,
        metric.grain,
        metric.displayFactor,
        metric.displayFactorSym
      );
    }
  }
});
