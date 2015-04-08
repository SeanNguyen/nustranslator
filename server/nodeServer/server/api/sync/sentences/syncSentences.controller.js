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
	var filePath = "./server/data/sentences/data.txt";
 	// logic here to determine what file, etc
	console.log("SYNC: new sync request");
	var rstream = fs.createReadStream(filePath);
	rstream.pipe(res);
};