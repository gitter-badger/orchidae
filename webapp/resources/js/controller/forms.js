'use strict';

(function() {
  var orchidae = angular.module('orchidae');

  orchidae.controller('FormsController', function($scope, $rootScope, $http,
          $location, transformRequestAsFormPost) {
    $scope.messages = [];
    $scope.data = {};
    $scope.submit = function(form, success) {
      var callback = (angular.isUndefined(success) || success === null)
              ? $scope.defaultCallback : success;
      $http({
        method: 'POST',
        url: './' + form + '.form',
        transformRequest: transformRequestAsFormPost,
        data: $scope.data,
        headers: {//With this trick we get the csrf up and running again
          'X-CSRF-TOKEN': document.getElementById("_csrf").value
        }
      }).success(callback).error(function(data, status, headers, config) {
        if (status == 400) {
          $scope.messages = data;
        } else {
          alert('Unexpected server error.');
        }
      });
    };

    $scope.setUserCallback = function(data, status, headers, config) {
      $scope.defaultCallback();
      $rootScope.user = $scope.data.username;
    }

    $scope.defaultCallback = function(data, status, headers, config) {
      $location.path('');
    }
  });
}());