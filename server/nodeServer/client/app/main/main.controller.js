'use strict';

angular.module('nodeServerApp')
  .controller('MainCtrl', function ($scope, $http) {
  	$scope.formData = [];
  	$scope.formData.push({language: "English", sentences: ""});
    $scope.formData.push({language: "Mandarin", sentences: ""});
    $scope.formData.push({language: "Vietnamese", sentences: ""});

    $scope.submit = function() {
      var data = JSON.stringify($scope.formData);
      console.log(data);
      $http.post('/api/update', data).
        success(function(data, status, headers, config) {
          // this callback will be called asynchronously
          // when the response is available
          window.alert("success");
        }).
        error(function(data, status, headers, config) {
          // called asynchronously if an error occurs
          // or server returns response with an error status.
          window.alert("error");
        });
    }
  });