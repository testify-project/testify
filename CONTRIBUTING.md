# Contributing

## Building from Source
Testify uses a Maven-based build system. To build from source follow the bellow instructions:

### Install Prerequisites
- [Git 1.9.1](https://git-scm.com/downloads) or above
- [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) (be sure to set `JAVA_HOME`)
- [Maven 3.3.3](https://maven.apache.org/download.cgi) or above
- [Docker 1.11.1](https://docs.docker.com/engine/installation)
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

### Check out sources
-- Via SSH (preferred for security reasons):
```
$ git clone git@github.com:testify-project/testify.git
```
- Or via HTTPS:

```
$ git clone https://github.com/testify-project/testify.git
```

### Compile, build, and install Testify JARs into your local Maven Cache
```
$ mvn install -Dmaven.test.skip
```

### Compile, test, build, and install Testify JARs into your local Maven Cache
```
$ mvn install
```

## Adding a Feature
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

## Issue Pull Request
[Pull requests](https://github.com/testify-project/testify/pulls) are welcome.