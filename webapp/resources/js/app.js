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