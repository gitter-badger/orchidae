'use strict';

(function () {
var orchidae = angular.module('orchidae');

    orchidae.controller('PhotostreamController', function ($scope, $rootScope, $http) {
        $scope.photos = [];
        $http.get('picture/admin/latest',{headers: {'Accept': 'application/json'}}).success(function(root) {
            $scope.photos = root;
        });
    });
}());