'use strict';

(function () {
var orchidae = angular.module('orchidae');

    orchidae.controller('PhotostreamController', function ($scope, $rootScope, $http, i18n, $location) {
        $scope.photos = [];
        $http.get('picture/admin/latest',{headers: {'Accept': 'application/json'}}).success(function(root) {
            $scope.photos = root;
            console.log($scope.photos);
        });
    });

}());