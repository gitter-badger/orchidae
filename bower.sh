#!/bin/sh
rm -rf bower_components
bower install
TARGET_JS='../webapp/resources/js/dist'
TARGET_CSS='../webapp/resources/css/dist'
TARGET_IMG='../webapp/resources/img/dist'
cd bower_components

#headroom for hiding header
cp headroom.js/dist/angular.headroom.min.js $TARGET_JS
cp headroom.js/dist/headroom.min.js $TARGET_JS

#angularjs core
cp angular/angular.min.js $TARGET_JS
cp angular/angular.min.js.map $TARGET_JS

#jquery core
cp jquery/dist/jquery.min.js $TARGET_JS
cp jquery/dist/jquery.min.map $TARGET_JS

#angularjs rount
cp angular-route/angular-route.min.js $TARGET_JS
cp angular-route/angular-route.min.js.map $TARGET_JS

#localization
cp jquery-i18n-properties/jquery.i18n.properties.js $TARGET_JS

#bootstrap
cp bootstrap/dist/css/bootstrap.css $TARGET_CSS
cp bootstrap/dist/css/bootstrap.css.map $TARGET_CSS

#flags
cp -r flag-icon-css/flags $TARGET_CSS/..
cp flag-icon-css/css/flag-icon.min.css $TARGET_CSS

#angular bootstrapui
cp angular-bootstrap/ui-bootstrap.min.js $TARGET_JS
