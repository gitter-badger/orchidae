'use strict';
(function () {
var orchidae = angular.module('orchidae');

    orchidae.controller('MainController', function ($scope, $rootScope, $http, i18n,$location) {
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
    });

}());

