'use strict';

var orchidae = {};

var App = angular.module('orchidae', ['ngRoute','headroom']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/upload', {
        templateUrl: '/upload.html',
        controller: SettingsController
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
