orchidae
========

Usage
====
to run orchidae simply call on the commandline `mvn spring-boot:run`, please see the setup for 3rd party content first. To create a distributable you need to invoke  `mvn package` to run it call `java -jar target/orchidae-1.0-SNAPSHOT.jar`

To peek into the jar created: `jar tvf target/orchidae-1.0-SNAPSHOT.jar`


3rd party libs
====
3rd party js stuff is installed through bower and grunt. To setup both please run

```
npm install
bower install
grunt
```


Powered with
====
Angular.js, headroom.js, MongoDB, Spring