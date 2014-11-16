#!/bin/sh
bower install
TARGET_JS='../webapp/resources/js/dist'
TARGET_CSS='../webapp/resources/css/dist'
cd bower_components
cp headroom.js/dist/angular.headroom.min.js $TARGET_JS
cp headroom.js/dist/headroom.min.js $TARGET_JS
cp angular/angular.min.js $TARGET_JS
cp angular/angular.min.js.map $TARGET_JS
cp jquery/dist/jquery.min.js $TARGET_JS
cp jquery/dist/jquery.min.map $TARGET_JS
cp angular-route/angular-route.min.js $TARGET_JS
cp angular-route/angular-route.min.js.map $TARGET_JS

cp bootstrap/dist/css/bootstrap.css $TARGET_CSS
cp bootstrap/dist/css/bootstrap.css.map $TARGET_CSS
