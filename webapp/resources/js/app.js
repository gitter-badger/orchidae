'use strict';

var orchidae = {};

var App = angular.module('orchidae', ['ngRoute','headroom']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/upload', {
        templateUrl: '/layout/upload',
        controller: SettingsController
    });
    $routeProvider.when('/', {
        templateUrl: '/layout/welcome'
    });
    $routeProvider.when('/login', {
        templateUrl: '/layout/login'
    });
    $routeProvider.when('/register', {
        templateUrl: '/layout/register'
    });
}]);
