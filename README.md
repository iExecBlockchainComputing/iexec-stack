# xtremweb-hep
Data driven volunteer cloud

### Status
[![Build Status](https://travis-ci.org/lodygens/xtremweb-hep.svg?branch=master)](https://travis-ci.org/lodygens/xtremweb-hep)

Quick start
===========

The easiest way is by using Docker.
Please see [Docker deployment](docker/master).

## Manual Build
### Pre-requisites

The following should be already install on the machine:
1. *make*
2. *ant*
3. *Java JDK* (The variable *JAVA_HOME* should also be set.)

### Build
[Gradle](http://gradle.org). is used to build the project. You can use either the native gradle command if gradle is available on your system:
```
gradle buildAll
```
or if gradle is not installed, you can use it's wrapped version, simply type:
```
./gradlew buildAll
```

### Run
After the build, in the folder *build/dist/xtremweb-X.Y.Y-SNAPSHOT/bin*, you can call the server, worker:

For the server:
```
./xtremweb.server console
```

For the worker:
```
./xtremweb.worker console
```
The client's commands can be called natively in the */bin* folder.
