'use strict';

var fs = require('fs');  // file system

exports.index = function(req, res) {
	console.log("SYNC DICT: new sync request");
	var filePath = './server/data/dict/';
	switch(req.body.language) {
		case 'english':
		filePath += "english.dic";
		break;
		
		case 'mandarin':
		filePath += "mandarin.dic";
		break;
		
		default:
		console.log("SYNC DICT: no language found:" + req.body.language);
		return;
	}
	console.log("SYNC DICT: querry" + filePath);
	var rstream = fs.createReadStream(filePath);
	rstream.pipe(res);
};