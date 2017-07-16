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

## [0.9.6-SNAPSHOT]
### Added
- Added Beta and Experimental annotations so users can be notified of beta and experimental features
- Added ability to update Javadoc for api and core on javadoc.io after deployment of artifacts
- Added the ability to get any annotation on the test class as well as those defined in`@Bundle` annotation from TestContext and TestDescriptor API
- Added ability to get all inspected annotations from the TestContext API
- Added @Strict, @Lenient, and @Loose guideline extention annotations
- Added Guidelines PreVerifier to insure online one guideline annotation is present.

### Changed
- Renamed UnitTest, IntegrationTest, and SystemTest extention annotations to UnitCategory, IntegrationCategory and SystemCategory avoid confusion

## [0.9.5] - 2017-06-14
### Added
- Added Injection of properties of resources and test context into test classes
 - Added @Property field annotation
 - Added ability to use expression language to access sub-graphs of properties
- Added ServerInstanceBuilder to build server instances
- Added createVirtualSut to MockProvider contract
- Added support for verifying interaction between the SUT and its collaborators
 - Added `verify` attribute to @Sut annotation
 - Added PostVerifier contract to perform verification after test case finish
 - Added InteractionPostVerifier implementation to verify all interaction
 - Added `verifyAllInteraction` method to MockProvider contract

### Changed
- Refactored ServerInstance and added FQN field and added ability to add properties to it
- Changed property names to camel-case to avoid properties being treated as expressions
- Renamed ConfigurationVerifier to PreVerifier
- Renamed WiringVerifier to PreiVerifier
- Renamed CollaboratorsReifier to InitialReifier
- Renamed TestReifier to FinalReifier

## [0.9.4] - 2017-06-06
### Changed
- Added configKey attribute to LocalResource, VirtualResource, RemoteResource to enable the association with configurations section in `.testify.yml` to a resource
- Added LocalResource and PropertiesReader parameters to LocalResourceProvider configure method
- Added VirtualResource and PropertiesReader parameters to VirtualResoruceProvider configure method
- Added RemoteResource and PropertiesReader parameters to RemoteResourceProvider configure method
- Added isEmpty method to PropertiesTraite

### Removed
 - Moved container module to its own [repository](https://github.com/testify-project/virtual-resources)

## [0.9.3] - 2017-05-14
### Added
- Added extention contracts and annotations to the API
 - Added @UnitTest, @IntegrationTest, and @SystemTest which can be used to apply specific services to certain test levels
 - Added TestReifer contract for reifying the test class
 - Added SutReifier contract for reifying the system under test
 - Added FieldReifier contract for reifying the test class fields
 - Added CollaboratorsReifier contract to reify cut class based on provided collaborators
- Added support for configuring behavior through ".testify.yml" configuration file
- Added PropertiesReader and PropertiesWriter to enable reading and writing of configuration properties
- Added @RemoteResource support

### Changed
- Adopted Semantic Testing
 - Renamed @Cut to @Sut
 - Renamed serverName and serverContract attributes in RequiresResource to resourceName and resourceContract
 - Renamed @RequiresResource annotation to @LocalResource
 - Renamed @RequiresContainer annotation to @VirtualResource
 - Renamed ResourceProvder contract to LocalResourceProvider and updated method signatures
 - Renamed ContainerProvider contract to VirtualResourceProvider and updated method signatures
 - Renamed ResourceInstance contract to LocalResourceInstance
 - Renamed ContainerInstance contract to VirtualResourceInstance
 - Renamed DefaultContainerInstance to DefaultVirtualResourceInstance
 - Renamed DefaultResourceInstance to DefaultLocalResourceInstance
 - Renamed ResourceInstanceBuilder to LocalResourceInstanceBuilder
 - Renamed ReificationProvider to TestResourcesProvider and updated method signatures
 - Renamed TestReifier to TestConfigurer

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
