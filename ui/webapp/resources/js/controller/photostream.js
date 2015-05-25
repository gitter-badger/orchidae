'use strict';

(function() {
  var orchidae = angular.module('orchidae');
  var headerDef = {
    headers: {
      'Accept': 'application/json'
    }
  };

  orchidae.controller('PhotostreamController', function($scope, $rootScope,
          $modal, $http) {
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

    $scope.open = function(photo_id) {
      var modalInstance = $modal.open({
        animation: true,
        templateUrl: 'myModalContent.html',
        controller: 'ModalInstanceCtrl',
        size: 'lg',
        resolve: {
          photo: function() {
            return photo_id;
          }
        }
      });
    };
  });

  orchidae.controller('ModalInstanceCtrl', function($scope, $modalInstance,
          photo) {

    $scope.photo = photo;
  });
}());
