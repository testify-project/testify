# Testify
[![Build Status](https://travis-ci.org/testify-project/testify.svg?branch=develop)](https://travis-ci.org/testify-project/testify)
[![Stories in Progress](https://badge.waffle.io/testify-project/testify.svg?label=In%20Progress&title=In%20Progress)](http://waffle.io/testify-project/testify)
[![Stories in Ready](https://badge.waffle.io/testify-project/testify.svg?label=ready&title=Ready)](http://waffle.io/testify-project/testify)
[![Join the chat on Gitter](https://badges.gitter.im/testify-project/Lobby.svg)](https://gitter.im/testify-project/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![CodecovIO](https://codecov.io/github/testify-project/testify/coverage.svg?branch=develop)](https://codecov.io/github/testify-project/testify?branch=develop)
[![Latest Release](https://img.shields.io/github/release/testify-project/testify.svg)](https://github.com/testify-project/testify/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fitbur.testify/parent/badge.svg?style=flat)](http://repo1.maven.org/maven2/com/fitbur/testify/)
[![License](https://img.shields.io/badge/license-Apache%20License%202-lightgrey.svg)](https://github.com/testify-project/testify/blob/develop/LICENSE)

[![Throughput Graph](https://graphs.waffle.io/testify-project/testify/throughput.svg)](https://waffle.io/testify-project/testify/metrics)

## Overview
Testify is an annotation driven Java Testing Framework that provides uniform and seamless way to write Unit, Integration, and System tests.

### Features
- Uniform Annotations for Unit, Integration and System Testing
- Managed Test Case Configuration, Isolation, and Execution
- JSR-330 Dependency Injection Annotations Support
- JUnit Testing Framework Support
- Pluggable Mocking SPI (Mockito and EasyMock supported)
- Pluggable Test Resource SPI (HSQL, ElasticSearch, ZooKeeper, etc supported)
- Pluggable Container Test Resource SPI (Docker Container supported)
- Pluggable Server SPI (Undertow Supported)
- Pluggable Client SPI (JAX-RS Client supported)
- Spring Integration Testing
- Spring Boot System Testing
- Spring Web MVC System Testing
- HK2 Integration Testing
- Jersey 2 RESTful Web Services Framework System Testing
- Google Guice Integration Testing

## Versioning
Testify has an automated release system and uses [Semantic Versioning][semver] version numbering system.

```
major.minor.patch
```

| number | meaning                                                                    |
| ------ | -------------------------------------------------------------------------- |
| major  | major version, with most probably incompatible change in API and behavior  |
| minor  | minor version, important enough change to bump this number                 |
| patch  | a released build number incremented automatically a pull request is merged |

## Learning
- Read [Java for Small Team][java-for-small-team] eBook.
- Take a look at [Test Driven Development][tdd-presentation] presentation.
- Testify documentation is available [here][docs].
- Examples code can be found [here][examples].

## Issue Tracking
Report issues via the [Github Issues][github-issues]. Think you've found a bug?
Please consider submitting a reproduction project via the a new [Github Issue][github-issues-new].

## Building from Source
Testify uses a Maven-based build system. To build from source follow the bellow instructions:

### Prerequisites
- [Git 1.9.1](https://git-scm.com/downloads) or above
- [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) (be sure to set `JAVA_HOME`)
- [Maven 3.0.5](https://maven.apache.org/download.cgi) or above
- [Docker 1.10.0](https://docs.docker.com/engine/installation) or above

### Check out sources
```
$ git clone git@github.com:testify-project/testify.git
```

or

```
$ git clone https://github.com/testify-project/testify.git
```

### Install all Testify jars into your local Maven cache
```
$ mvn install -Dmaven.test.skip
```

### Compile and test and build all jars
```
$ mvn clean install
```

## Contributing

### Prerequisites
- [Install GitFlow](http://danielkummer.github.io/git-flow-cheatsheet)
- Initialize GitFlow:
```bash
$ git flow init
Branch name for production releases: master
Branch name for "next release" development: develop
Feature branch prefix: feature/
Bugfix branch prefix:
Release branch prefix: release/
Hotfix branch prefix: hotfix/
Support branch prefix: support/
Version tag prefix:
```

### Adding a Feature
- Create a feature:
```bash
$ git flow feature start awesome-feature
```
- Do some development and commit to awesome-feature branch:
```bash
$ git commit -m "awesome-feature description" .
```
- Publish feature:
```bash
$ git flow feature publish awesome-feature
```
- Finish the feature:
```bash
$ git flow feature finish awesome-feature
```

### Performing Release
- Start release:
```bash
# replace x.x.x with release semantic version
$ git flow release start x.x.x
```
- Update the project version in pom files:
```bash
# replace x.x.x with release semantic version
$ mvn versions:set -DnewVersion=x.x.x
```
- Commit the updated pom files:
```bash
# replace x.x.x with release semantic version
$ git commit -m "Updated version to x.x.x" .
```
- Finish the release:
```bash
# replace x.x.x with release semantic version
$ git flow release finish x.x.x # release semantic version
```
- Update next development project version in pom files:
```bash
# replace x.x.x with next development semantic version
$ mvn versions:set -DnewVersion=x.x.x-SNAPSHOT
```
- Commit the updated pom files:
```bash
# replace x.x.x with next development semantic version
$ git commit -m "Updated next development version to x.x.x-SNAPSHOT" .
```
- Push changes in develop and master branches and tags to remote repository:
```bash
$ git push origin develop master --tags
```

### Issue Pull Request
[Pull requests](http://help.github.com/send-pull-requests) are welcome.

## Staying in Touch
Hit us up on [Gitter][gitter].

## License
The Testify is released under [Apache Software License, Version 2.0](LICENSE).

Enjoy and keep on Testifying!


[semver]: http://semver.org
[docs]: http://fitburio.github.io/testify
[examples]: https://github.com/testify-project/examples/tree/develop/examples
[github-issues]: https://github.com/testify-project/testify/issues
[github-issues-new]: https://github.com/testify-project/testify/issues/new
[gitter]: https://gitter.im/testify-project/testify
[java-for-small-team]: https://www.gitbook.com/book/ncrcoe/java-for-small-teams/details
[tdd-presentation]: http://saden1.slides.com/saden1/tdd/embed?token=C82lw8_l
