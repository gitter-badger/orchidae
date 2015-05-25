'use strict';
(function() {
  var orchidae = angular.module('orchidae');
  orchidae.directive('dropzone', function() {
    return function(scope, element, attrs) {
      var config, dropzone;

      config = scope[attrs.dropzone];

      // create a Dropzone for the element with the given options
      dropzone = new Dropzone(element[0], config.options);

      // bind the given event handlers
      angular.forEach(config.eventHandlers, function(handler, event) {
        dropzone.on(event, handler);
      });
    };
  });
  orchidae.controller('dropzoneCtrl', function($scope, $http) {
    $scope.dropzoneUpload = {
      'options': { // passed into the Dropzone constructor
        url: "/picture", // Set the url
        previewsContainer: "#previews", // Define the container to display the previews
        acceptedMimeTypes: "image/*",
        addRemoveLinks: "true"
      },
      'eventHandlers': {
        'success': function(file, response) {
          file.picId = response.ids[0];
        },
        'removedfile': function(file) {
          if (file.picId != null) {
            //if the file was uploaded already, we need to call for removal
            $http({
              method: 'DELETE',
              url: './picture/admin/' + file.picId,
              headers: {//With this trick we get the csrf up and running again
                'X-CSRF-TOKEN': document.getElementById("_csrf").value
              }
            })
          }
        }
      }
    };
  });

}());