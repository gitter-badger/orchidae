'use strict';

var orchidae = angular.module('orchidae', ['ngRoute','headroom','ui.bootstrap']);

// Declare app level module which depends on filters, and services
orchidae.config(['$routeProvider', function ($routeProvider) {
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
}]);

//main controller, controlling some basic side stuff
(function () {
var orchidae = angular.module('orchidae');

    orchidae.controller('MainController', function ($scope, $rootScope, $http, i18n, $location) {
        $scope.language = function () {
            return i18n.language;
        };
        $scope.setLanguage = function (lang) {
            var langElement =angular.element(document.querySelector('#langToggle'));
            langElement.removeClass("flag-icon-" + i18n.language);
            langElement.addClass("flag-icon-" + lang);
            i18n.setLanguage(lang);
        };

        $scope.availLang = function () {
            return ['us','de']
        };

        $scope.isActive = function (viewLocation) {
                return viewLocation === $location.path();
        };
    });

}());
