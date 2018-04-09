const fs = require('fs');
const { validateFileDB } = require('iexec-schema-validator');
const { stringArrayToJS } = require('./utils');

const dappsDB = fs.readFileSync('dapps-db.js', 'utf8');

const allDapps = stringArrayToJS(dappsDB);
console.log = () => {};
const dapps = allDapps.filter(e => e.type === 'dapp');

test('data schema', () =>
  dapps.forEach(dapp => expect(validateFileDB(dapp)).toBeTruthy()));
