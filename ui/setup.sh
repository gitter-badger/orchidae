#!/bin/bash
echo "installing bower"
npm install -g bower
echo "installing grunt"
npm install -g grunt-cli
npm install
./node_modules/protractor/bin/webdriver-manager update --standalone
echo "installing orchidae dependencies"
bower install
grunt
echo "setup done. you can resume with"
echo "creating symlink to ease development"
ln -s "$(pwd)/webapp" ../server/webapp
echo "Run the application: mvn exec:java or through your favorite IDE"