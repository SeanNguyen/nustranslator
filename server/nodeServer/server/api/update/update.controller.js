/**
 * Using Rails-like standard naming convention for endpoints.
 * GET     /things              ->  index
 * POST    /things              ->  create
 * GET     /things/:id          ->  show
 * PUT     /things/:id          ->  update
 * DELETE  /things/:id          ->  destroy
 */

'use strict';

var fs = require('fs');  // file system

exports.index = function(req, res) {
  // logic here to determine what file, etc
  var rstream = fs.createReadStream('./server/data/data.txt');
  rstream.pipe(res);
};