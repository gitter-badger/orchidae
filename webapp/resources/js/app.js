'use strict';

var orchidae = angular.module('orchidae', ['ngRoute', 'headroom',
    'ui.bootstrap']);

// Declare app level module which depends on filters, and services
orchidae.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/upload', {
    templateUrl: '/upload.html',
  });
  $routeProvider.when('/', {
    templateUrl: '/welcome.html'
  });
  $routeProvider.when('/login', {
    templateUrl: '/login.html'
  });
  $routeProvider.when('/register', {
    templateUrl: '/register.html'
  });
  $routeProvider.when('/stream', {
    templateUrl: '/stream.html'
  });
}]);

//main controller, controlling some basic side stuff
(function() {
  var orchidae = angular.module('orchidae');

  orchidae.controller('MainController', function($scope, $rootScope, $http,
          i18n, $location) {
    $scope.language = function() {
      return i18n.language;
    };
    $scope.setLanguage = function(lang) {
      var langElement = angular.element(document.querySelector('#langToggle'));
      langElement.removeClass("flag-icon-" + i18n.language);
      langElement.addClass("flag-icon-" + lang);
      i18n.setLanguage(lang);
    };

    $scope.availLang = function() {
      return ['us', 'de']
    };

    $scope.isActive = function(viewLocation) {
      return viewLocation === $location.path();
    };
  });

  orchidae.filter('reverse', function() {
    return function(items) {
      return items.slice().reverse();
    };
  });

  //form like processing of request based on: http://www.bennadel.com/blog/2615-posting-form-data-with-http-in-angularjs.htm
  orchidae
          .factory(
                  "transformRequestAsFormPost",
                  function() {
                    // I prepare the request data for the form post.
                    function transformRequest(data, getHeaders) {
                      var headers = getHeaders();
                      headers["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8";
                      return (serializeData(data));
                    }

                    // Return the factory value.
                    return (transformRequest);

                    // I serialize the given Object into a key-value pair string. This
                    // method expects an object and will default to the toString() method.
                    // --
                    // NOTE: This is an atered version of the jQuery.param() method which
                    // will serialize a data collection for Form posting.
                    // --
                    // https://github.com/jquery/jquery/blob/master/src/serialize.js#L45
                    function serializeData(data) {
                      // If this is not an object, defer to native stringification.
                      if (!angular.isObject(data)) { return ((data == null)
                              ? "" : data.toString()); }

                      var buffer = [];

                      // Serialize each key in the object.
                      for ( var name in data) {
                        if (!data.hasOwnProperty(name)) {
                          continue;
                        }
                        var value = data[name];
                        buffer.push(encodeURIComponent(name)
                                + "="
                                + encodeURIComponent((value == null) ? ""
                                        : value));
                      }

                      // Serialize the buffer and clean it up for transportation.
                      var source = buffer.join("&").replace(/%20/g, "+");

                      return (source);
                    }
                  });
}());