'use strict';

(function () {
var orchidae = angular.module('orchidae');

    orchidae.controller('FormsController', function ($scope, $rootScope, $http, $location, transformRequestAsFormPost) {
    $scope.messages = [];
        $scope.data = {};
        $scope.submit = function(form) {
        $http({
          method: 'POST', url: './' + form + '.form',
          transformRequest: transformRequestAsFormPost,
          data: $scope.data
        }).
          success(function(data, status, headers, config) {
            $location.path('');
          }).
          error(function(data, status, headers, config) {
            if(status == 400) {
              $scope.messages = data;
            } else {
              alert('Unexpected server error.');
            }
          });
          };
    });
}());