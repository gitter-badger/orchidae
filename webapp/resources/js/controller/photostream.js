'use strict';

(function() {
  var orchidae = angular.module('orchidae');
  var headerDef = {
    headers: {
      'Accept': 'application/json'
    }
  };

  orchidae.controller('PhotostreamController', function($scope, $rootScope,
          $http) {
    $scope.photos = [];
    $scope.totalItems = 0;
    $scope.numPerPage = 200;
    $scope.maxSize = 5;
    $scope.currentPage = 0;

    $http.get('picture/' + $rootScope.user + '/count', headerDef).success(
            function(root) {
              $scope.totalItems = root.count;
            });

    $scope.$watch('currentPage', function() {
      var skip = (($scope.currentPage - 1) * $scope.numPerPage);

      $http.get(
              'picture/' + $rootScope.user + '/latest?n=' + $scope.numPerPage
                      + "&s=" + skip, headerDef).success(function(root) {
        $scope.photos = root;
      })
    });
  });
}());
