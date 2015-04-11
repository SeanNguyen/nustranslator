/**
 * Main application routes
 */

'use strict';

var errors = require('./components/errors');
// var resultSocket = require('./api/record/result');
// var voiceSocket = require('./api/record/voice');

module.exports = function(app) {
  var express = require('express');
  var serveIndex = require('serve-index');
  // Insert routes below
  app.use('/api/update', require('./api/update'));
  app.use('/api/sync/sentences', require('./api/sync/sentences'));
  app.use('/api/sync/dict', require('./api/sync/dict'));
  app.use('/api/sync/language_model', require('./api/sync/language_model'));
  app.use('/view_record', express.static(__dirname + "/data/records"));
  app.use('/view_record', serveIndex(__dirname + "/data/records"));

  // All undefined asset or api routes should return a 404
  app.route('/:url(api|auth|components|app|bower_components|assets)/*')
   .get(errors[404]);

  // All other routes should redirect to the index.html
  app.route('/*')
    .get(function(req, res) {
      res.sendfile(app.get('appPath') + '/index.html');
    });
};
