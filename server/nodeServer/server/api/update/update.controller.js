/**
 * Using Rails-like standard naming convention for endpoints.
 * GET     /things              ->  index
 * POST    /things              ->  create
 * GET     /things/:id          ->  show
 * PUT     /things/:id          ->  update
 * DELETE  /things/:id          ->  destroy
 */
'use strict';

exports.index = function(req, res) {
	var filePath = "./server/data/sentences/data.txt";
	//read current data
	var fs = require('fs');
	var obj;
	var versionNumber = 1;
	fs.readFile(filePath, 'utf8', function (err, data) {
		if (!err) {
			obj = JSON.parse(data);
			console.log(obj);
			var oldVersion = obj.version;
			if (!err && oldVersion >= 0) {
				versionNumber = oldVersion + 1;
			}
		}		  
		//write new data
		var newData = {};
		newData["version"] = versionNumber;
		newData["data"] = req.body;
		fs.writeFile(filePath, JSON.stringify(newData), function (err) {
		  if (err) {
			res.json([{ result : 'fail' }]);
		  	console.log(err);
		  }
		  console.log("API UPDATE: data updated");
		});

		//return result
		res.json([{ result : 'success' }]);
	});
}