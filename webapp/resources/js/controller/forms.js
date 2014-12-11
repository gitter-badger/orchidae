'use strict';

(function() {
  var orchidae = angular.module('orchidae');

  orchidae.controller('FormsController', function($scope, $rootScope, $http,
          $location, transformRequestAsFormPost) {
    $scope.messages = [];
    $scope.data = {};
    $scope.submit = function(form) {
      $http({
        method: 'POST',
        url: './' + form + '.form',
        transformRequest: transformRequestAsFormPost,
        data: $scope.data
      }).success(function(data, status, headers, config) {
        $location.path('');
      }).error(function(data, status, headers, config) {
        if (status == 400) {
          $scope.messages = data;
        } else {
          alert('Unexpected server error.');
        }
      });
    };
    //TODO would be cool if only custom callback could be provided
    $scope.login = function() {
      $http({
        method: 'POST',
        url: './login.form',
        transformRequest: transformRequestAsFormPost,
        data: $scope.data
      }).success(function(data, status, headers, config) {
        $location.path('');
        $rootScope.user = $scope.data.username;
      }).error(function(data, status, headers, config) {
        if (status == 400) {
          $scope.messages = data;
        } else {
          alert('Unexpected server error.');
        }
      });
    };
  });
}());