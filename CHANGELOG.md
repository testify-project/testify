# Change Log
All notable changes to this project will be documented in this file. This project
adheres to [Semantic Versioning](http://semver.org/). The change log file consists
of sections listing each version and the date they were released along with what
was added, changed, deprecated, removed, fix and security fixes.

- Added - Lists new features
- Changed - Lists changes in existing functionality
- Deprecated -  Lists once-stable features that will be removed in upcoming releases
- Removed - Lists deprecated features removed in this release
- Fixed - Lists any bug fixes
- Security - Lists security fixes to security vulnerabilities

## [Unreleased]

## [0.9.3-SNAPSHOT]
### Changed
- Adopted Semantic Testing
  - Changed serverName and serverContract attributes in RequiresResource to resourceName and resourceContract
  - Renamed @RequiresResource annotation to @LocalResource
  - Renamed @RequiresContainer annotation to @VirtualResource
  - Renamed ResourceProvder contract to LocalResourceProvider
  - Renamed ContainerProvider contract to VirtualResourceProvider
  - Renamed ResourceInstance contract to LocalResourceInstance
  - Renamed ContainerInstance contract to VirtualResourceInstance
  - Renamed DefaultContainerInstance to DefaultVirtualResourceInstance
  - Renamed DefaultResourceInstance to DefaultLocalResourceInstance
  - Renamed ResourceInstanceBuilder to LocalResourceInstanceBuilder

## [0.9.2] - 2017-03-20
### Fixed
- Bug in SpringBootServerProvider when trying to add a new element to an immutable map
- Bug in HK2ServiceInstance that prevented the removal and replacement services
- Bug in ResourceInstanceBuilder where builder methods used super instead of extends

## [0.9.1] - 2017-03-19
### Fixed
- Bug were deployment of artifact to sonatype failed due to [missing main artifact](https://travis-ci.org/testify-project/testify/builds/212702576)

## [0.9.0] - 2017-03-19
### Added
- API module that defines Testify API contracts and annotations
- Core module that implements and provides:
 - Common implementation of API contracts
 - Test class and class under test analyzers
 - Utility classes
- Mockito and EasyMock implementations of the MockProvider contract
- Docker implementation of ContainerProvider contract
- Spring, HK2, and Guice DI implementations of the ServiceProvider contract
- Unit, Integration, and System test implementations of TestRunner and TestVerifier contracts
- JUnit 4 test runner implementations for Spring, HK2, Guice, Spring Boot, Spring Servlet MVC, Jersey 2
- Jersey Client implementation of the ClientProvider contract
- Undertow implementation of the ServerProvider contract

