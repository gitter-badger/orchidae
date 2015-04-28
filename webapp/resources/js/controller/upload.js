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
  orchidae.controller('dropzoneCtrl', function($scope) {
    $scope.dropzoneUpload = {
      'options': { // passed into the Dropzone constructor
        url: "/picture", // Set the url
        previewsContainer: "#previews", // Define the container to display the previews
        acceptedMimeTypes: "image/*"
      },
      'eventHandlers': {
        'sending': function(file, xhr, formData) {
        },
        'success': function(file, response) {
        }
      }
    };
  });

}());