# Releasing

## Versioning
Testify has an automated release system and uses [Semantic Versioning](http://semver.org) version numbering system. Production release versions should conform semantic versioning scheme (i.e. 0.9.1) and development release version should conform semantic versioning scheme along with SNAPSHOT qualifier (i.e. 0.9.2-SNAPSHOT)

```
major.minor.patch
```

| number | meaning                                                                    |
| ------ | -------------------------------------------------------------------------- |
| major  | major version, with most probably incompatible change in API and behavior  |
| minor  | minor version, important enough change to bump this number                 |
| patch  | a released build number incremented automatically a pull request is merged |

## Performing Release
- Start release:
```bash
RELEASE_VERSION=x.x.x
DEVELOPMENT_VERSION=x.x.x-SNAPSHOT
$ git flow release start $RELEASE_VERSION
```
- Update the project version in pom files:
```bash
$ mvn versions:set -DnewVersion=$RELEASE_VERSION
```
- Update CHANGELOG.md:
- Commit the changes:
```bash
$ git commit -m "Prepare release $RELEASE_VERSION" .
```
- Finish the release:
```bash
$ git flow release finish $RELEASE_VERSION
```
- Update next development project version in pom files:
```bash
$ mvn versions:set -DnewVersion=$DEVELOPMENT_VERSION
```
- Commit the updated pom files:
```bash
$ git commit -m "Updated next development version to $DEVELOPMENT_VERSION" .
```
- Push changes, develop, master branches and tags to remote repository:
```bash
$ git push origin develop master --tags
```
