'use strict';

var fs = require('fs');  // file system

exports.index = function(req, res) {
	console.log("SYNC LANGUAGE MODEL: new sync request");
	var filePath = './server/data/language_model/';
	switch(req.body.language) {
		case 'english':
		filePath += "english.lm";
		break;
		
		case 'mandarin':
		filePath += "mandarin.lm";
		break;
		
		default:
		console.log("SYNC LANGUAGE MODEL: no language found:" + req.body.language);
		return;
	}
	console.log("SYNC LANGUAGE MODEL: querry" + filePath);
	var rstream = fs.createReadStream(filePath);
	rstream.pipe(res);
};