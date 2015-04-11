/**
 * Main application file
 */

 'use strict';

// Set default node environment to development
process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var express = require('express');
var config = require('./config/environment');
// Setup server
var app = express();
var 
server = require('http').createServer(app);
require('./config/express')(app);
require('./routes')(app);

// Start server
server.listen(config.port, config.ip, function () {
	console.log('Express server listening on %d, in %s mode', config.port, app.get('env'));
});

//init socket
var io = require('socket.io')(server);
io.on('connection', function(socket){
	console.log('SOCKET: user connnect');
	var filePath = './server/data/records/';
	var fileExt = '';
	var fs = require('fs');
	var writeStream ;
	var atob = require('atob')
		
	socket.on('disconnect', function(){
		if (writeStream) {
			writeStream.end();
		}
		console.log('SOCKET: user disconnect');
	});
	socket.on('dataType', function(message) {
		console.log('SOCKET: data type: ' + message);
		if (message == 'text') {
			fileExt = '.txt';
		} else if (message == 'audio') {
			fileExt = '';
		}
	});
	socket.on('fileName', function(message) {
		console.log('SOCKET: file name: ' + message + fileExt);
		writeStream = fs.createWriteStream(filePath + message + fileExt);
	});
	socket.on('data', function(message) {
		if (fileExt == '.txt') {
			writeStream.write(message);
			writeStream.write('\n');
		} else if (fileExt == '') {
			message = atob(message);
			writeStream.write(message);
		}
	});
});

// Expose app
exports = module.exports = app;