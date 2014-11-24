﻿﻿'use strict';

module.exports = function (grunt) {
    // Load the plugins
    grunt.loadNpmTasks('grunt-contrib-copy');


	// Declare vars
	var
		bowerDir = './bower_components/',
		distDir = 'webapp/resources/dist/',
		cssDir= distDir + "css",
		jsDir = distDir + "js",
		imgDir = distDir + "img";


    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
		appName: function () {
			var input = grunt.template.process('<%= pkg.name %>');
			return input.toLowerCase().replace(/[-\.](.)/g, function(match, group1) {
				return group1.toUpperCase();
			});
		},
		contributors: function () {
			var out = '';
			grunt.config.get('pkg.contributors').forEach(function (c) {
				var hasName = (typeof c.name === "string");
				var hasEmail = (typeof c.email === "string");
				if (!hasName && !hasEmail) {
					return '';
				}

				var s = ' *          ';
				if (hasName) {
					s = s.concat(c.name);
				}
				if (hasEmail) {
					s = s.concat(s.length > 0 ? ' ' : '').concat('<' + c.email + '>');
				}
				out = out.concat(out.length > 0 ? ',\n' : '').concat(s);
			});
			return out;
		},
		copy: {
			main: {
				files: [
					{//angular core
						expand: true,
						cwd: bowerDir + 'angular/',
						src: ['angular.min.js', 'angular.min.js.map'],
						dest: jsDir
					},
					{//angular route
						expand: true,
						cwd: bowerDir + 'angular-route/',
						src: ['angular-route.min.js', 'angular-route.min.js.map'],
						dest: jsDir
					},
					{//jquery core
						expand: true,
						cwd: bowerDir + 'jquery/dist',
						src: ['jquery.min.js', 'jquery.min.map'],
						dest: jsDir
					},
					{//jquery i18n
						expand: true,
						cwd: bowerDir + 'jquery-i18n-properties',
						src: ['jquery.i18n.properties.js'],
						dest: jsDir
					},
					{//bootstrap css
						expand: true,
						cwd: bowerDir + 'bootstrap/dist/css',
						src: ['bootstrap.css', 'bootstrap.css.map'],
						dest: cssDir
					},
					{//angular bootstrap
						expand: true,
						cwd: bowerDir + 'angular-bootstrap',
						src: ['ui-bootstrap.min.js'],
						dest: jsDir
					},
					{//flag icons
						expand: true,
						cwd: bowerDir + 'flag-icon-css/flags',
						src: ['**'],
						dest: distDir + 'flags'
					},
					{//flag icon css
						expand: true,
						cwd: bowerDir + 'flag-icon-css/css',
						src: ['flag-icon.min.css'],
						dest: cssDir
					},
					{//headroom.js
						expand: true,
						cwd: bowerDir + 'headroom.js/dist',
						src: ['angular.headroom.min.js', 'headroom.min.js'],
						dest: jsDir
					},
					{//dropzone js
						expand: true,
						cwd: bowerDir + 'dropzone/downloads/',
						src: ['dropzone.min.js'],
						dest: jsDir
					},
					{//dropzone css
						expand: true,
						cwd: bowerDir + 'dropzone/downloads/css',
						src: ['dropzone.css', 'basic.css'],
						dest: cssDir
					}
					]
			}
		},
    });

    // Default task(s).
    grunt.registerTask('default', ['copy:main']);
};