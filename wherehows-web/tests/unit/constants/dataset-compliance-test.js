import { module, test } from 'qunit';
import {
  getComplianceSteps,
  complianceSteps,
  getFieldIdentifierOption,
  getFieldIdentifierOptions
} from 'wherehows-web/constants';
import complianceDataTypes from 'wherehows-web/mirage/fixtures/compliance-data-types';

module('Unit | Constants | dataset compliance');

test('getComplianceSteps function should behave as expected', function(assert) {
  assert.expect(3);
  const piiTaggingStep = { 0: { name: 'editDatasetLevelCompliancePolicy' } };
  let result;

  assert.equal(typeof getComplianceSteps, 'function', 'getComplianceSteps is a function');
  result = getComplianceSteps();

  assert.deepEqual(result, complianceSteps, 'getComplianceSteps result is expected shape when no args are passed');

  result = getComplianceSteps(false);
  assert.deepEqual(
    result,
    { ...complianceSteps, ...piiTaggingStep },
    'getComplianceSteps result is expected shape when hasSchema attribute is false'
  );
});

test('getFieldIdentifierOption function should behave as expected', function(assert) {
  const [complianceType] = complianceDataTypes;
  let result;

  assert.equal(typeof getFieldIdentifierOption, 'function', 'getFieldIdentifierOption is a function');
  result = getFieldIdentifierOption(complianceType);

  assert.ok(result.label === complianceType.title, 'title matches the resulting label');
  assert.ok(result.value === complianceType.id, 'id matches the resulting value');
});

test('getFieldIdentifierOptions function should behave as expected', function(assert) {
  let results;
  assert.equal(typeof getFieldIdentifierOptions, 'function', 'getFieldIdentifierOptions is a function');
  results = getFieldIdentifierOptions(complianceDataTypes);

  assert.ok(Array.isArray(results), 'getFieldIdentifierOptions returns an array');

  results.forEach((result, index) => {
    assert.ok(result.label === complianceDataTypes[index].title, 'title matches the resulting label');
    assert.ok(result.value === complianceDataTypes[index].id, 'id matches the resulting value');
  });
});
