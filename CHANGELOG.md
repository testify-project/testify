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
### Added
- API module which defines contracts and annotations
- Core module which implements and provides common implementation of API contracts, utility classes as well as the ability to parse test and class under test classes
- Mockito and EasyMock implementations of the MockProvider contract
- Docker implementation of ContainerProvider contract
- Spring, HK2, and Guice DI implementations of the ServiceProvider contract
- Unit, Integration, System test implementation TestRunner and TestVerifier contracts
- JUnit test runner implementations for Spring, HK2, Guice, Spring Boot, Spring Servlet MVC, Jersey 2
- JAX-RS implementation of the ClientProvider contract
- Undertow implementation of the ServerProvider contract
- Tools module that contains various helper tools (generate services, providing logging, test categories)
- External module that provides shaded libs
