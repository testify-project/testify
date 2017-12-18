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

## [1.0.0] - 2017-12-18
### Added
- Added Java Agent to enable the redefinition of classes to the core module
 - Added InstrumentProvider and InstrumentInstance to enable the redefinition of classes
 - Added support for rebasing and intercepting constructor invocations
- Introduced InstanceProvider extension contracts that enable the addition of arbitrary constants
- Introduced ProxyInstanceProvider and ProxyInstanceControler to enable the creation and addition of proxied constants
- Added support for generic JUnit 4 system test runner
- Added a default generic server provider to enable direct management of application test lifecycle
 - Added `start()` and `stop` attributes to `@Application` annotation
 - Added ability to start a generic server using static void main functions
 - Added ability to start a generic server using arbitrary start and stop functions
- Added Grizzly 2 server support
- Added RemoteResourcePreVerifier to verify remote resources
- Added GRPC Support

### Changed
- Renamed TestResourceProvider to ResourceController
- Added the ability to get client type and client provider type to ClientProvider contract
- Added the ability to get server type to the ServerProvider contract
- Moved reifier and verify extension classes to the extension package
- Moved @Discoverable from build-tools:service-generator to the API module
- Renamed PreiVerifier to Verifier
- Changed how errors and warnings are reported by verifiers
 - Added `addWarning`, `addError`, and `verify` methods to the TestContext
 - Removed dependency verification from the test runners to PreVerifier implementation
 
### Removed 
- Removed the ability to use the @Fixture annotation on classes in favor of @Module#test attribute
- Removed framework specific GuiceIntegrationTest, HK2InegrationTest, JerseySystemTest, SpringBootSystemTest, SpringIntegrationTest, and SpringSystemTest test runners in favor of generic UnitTest, IntegrationTest, and SystemTest test runners.

## [0.9.9] - 2017-09-15
### Added
- Added new `@Name` annotation that can be used to associate a custom name with a field, method or method parameter
- Added the ability to find and inject collaborator method arguments by name
- Added `MethodDescriptor#getDeclaredName()` to return the name of the method based on the name of the method or the one specified by `@Name` annotation
- Added the ability to find collaborator method by name and return type to `TestDescriptor`
- Added the ability to compose collaborator providers by allowing `@CollaboratorProvder` to be annotated with `@CollaboratorProvder`
- Added `PreInstanceProvider`, `InstanceProvider`, `PostInstanceProvider` contracts to enable the addition of custom constants to the `ServiceInstance`
- Added `@Hint` annotation to provide hints during test execution (i.e. `ServiceProvider` implementation)
- Added `ClientInstanceBuilder` class to build client instances.
- Added the ability to get local, virtual and remote resources from the `TestContext`
- Added a method to get application annotation from `ServerInstance` contract
- Added a default implementation `DefaultServiceInstance` of `ServiceInstance` contract that can be used in unit tests
- Added `RemoteResourceInfo`, `LocalResourceInfo`, and `VirtualResourceInfo` contracts
- Added methods to `TestContext` to get local, remote, and virtual resources.
- Added `DefaultRemoteResourceInfo`, `DefaultLocalResourceInfo`, and `DefaultVirtualResourceInfo` implementations to replace `DefaultResourceInstance`

### Changed
- Renamed `getDefinedName` method `FieldDescriptor` and `MethodDescriptor` to `getDeclaredName`
- Changed `configure` and `start` methods in `ServerProvider` contract by adding `@Applicaiton` annotation parameter
- Changed `configure` and `create` methods in `ClientProvider` contract by adding `@Applicaiton` annotation parameter
- Changed stop method in `ClientProvider` contract by adding `ClientInstance` parameter
- Changed ClientInstance contract to return client and clientProvider instances. Also added methods to get `fqn` and `annotation`.
- Renamed `PropertiesWriter#addListElement` and `PropertiesReader#findList`
- Changed return types of methods that return `List` to `Collection` 
- Renamed `ResourceInstance` contract to `ResourceInfo`

### Removed
- Removed `name` attribute from `@Fake`, `@Virtual` and `@Real`. `@Named` can be used on the field as a replacement.
- Removed `DefaultResourceInstance`

## [0.9.8] - 2017-08-12
### Added
- Added the ability to create SUT using a factory method within the SUT class
 - Added `factoryMethod` attribute to the `@Sut` annotation
- Added the ability to specify multiple collaborator providers by changing `@CollaboratorProvder#value` to an array of classes

## [0.9.7] - 2017-07-29
### Added
- Added support for loading data into resources through `#load(...)` method defined in `LocalResourceProvider`, `RemoteResourceProvider`, and `VirtualResourceProvider` contracts
- Added the ability to load data via a `DataProvider` contract
- Added `dataFiles` and `dataProvider` attributes to `LocalResource`, `RemoteResource` and `VirtualResource` annotations.
- Added new `ExpressionUtil` class for working with expressions
- Added `env` attribute to `VirtualResource` annotation to pass environmental variables to virtual resources
- Added `ports` attribute to `VirtualResource` annotation to specify which ports should be reachable

### Changed
- Changed the default value of `VirtualResource#maxRetries` to 5 from 3
- Changed the default value of `VirtualResource#maxDuration` to 32000 from 8000 ms
- Updated dependency artifact versions

## [0.9.6] - 2017-07-16
### Added
- Added Beta and Experimental annotations so users can be notified of beta and experimental features
- Added ability to update Javadoc for api and core on javadoc.io after deployment of artifacts
- Added the ability to get any annotation on the test class as well as those defined in`@Bundle` annotation from TestContext and TestDescriptor API
- Added ability to get all inspected annotations from the TestContext API
- Added @Strict, @Lenient, and @Loose guideline extention annotations
- Added Guidelines PreVerifier to insure online one guideline annotation is present.

### Changed
- Renamed UnitTest, IntegrationTest, and SystemTest extention annotations to UnitCategory, IntegrationCategory and SystemCategory avoid confusion
- Renamed FieldReifier to CollaboratorReifier

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
