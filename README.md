orchidae [![Build Status](https://travis-ci.org/cherimojava/orchidae.svg?branch=master)](https://travis-ci.org/cherimojava/orchidae) [![Coverage Status](https://coveralls.io/repos/cherimojava/orchidae/badge.svg?branch=master)](https://coveralls.io/r/cherimojava/orchidae?branch=master)
========

Usage
====
to run orchidae simply call on the commandline `mvn exec:java` or start the `Starter.java` class within your favourite IDE, please see the setup for 3rd party content first. To create a distributable you need to invoke  `mvn package` to run it call `java -jar target/orchidae-1.0-SNAPSHOT.jar`

To peek into the jar created: `jar tvf target/orchidae-1.0-SNAPSHOT.jar`

To run the application please simply call run.sh to install all required non maven dependencies.
One the application is running you can use it under `localhost:8082`, for the first time you need to login/register a user, which you can later on use to login.


Powered with
====
Angular.js, MongoDB, Spring
