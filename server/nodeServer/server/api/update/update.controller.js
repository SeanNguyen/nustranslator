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
		  console.log("API UPDATE: sentences updated");
		});
	});

	//update language model by running some command line
	for (var i = req.body.length - 1; i >= 0; i--) {
		var language = req.body[i].language;
		var fileContent = '';
		var rawContent = req.body[i].sentences;
		var lines = rawContent.split('\n');
		for (var i = lines.length - 1; i >= 0; i--) {
			fileContent += "<s> " + lines[i] + " </s>\n";
		};
		fileContent.toLowerCase();

		fs.writeFile(language + '.txt', fileContent, function(err) {
			if (err) {
				console.log('API UPDATE: writing language model file fail');
				res.json([{ result : 'fail' }]);
			} else {
				var exec = require('child_process').exec;
				exec('./server/data/cmuclmtk/text2wfreq < ' + language + '.txt | ./server/data/cmuclmtk/wfreq2vocab > ' + language + '.vocab; ./server/data/cmuclmtk/text2idngram -vocab ' + language + '.vocab -idngram ' + language + '.idngram < ' + language + '.txt; ./server/data/cmuclmtk/idngram2lm -vocab_type 0 -idngram ' + language + '.idngram -vocab ' + language + '.vocab -arpa server/data/language_model/' + language.toLowerCase() + '.lm', 
				function(error, stdout, stderr) {
					if(error) {
						console.log('API UPDATE: convert to lm file fail')
						res.json([{ result : 'fail' }]);
					} else {
						console.log('API UPDATE: converted: ' + language);
					}
				});
			}
			
		})
	};
	res.json([{result: 'success'}]);
}