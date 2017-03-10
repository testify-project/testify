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
# replace x.x.x with release semantic version
$ git flow release start x.x.x
```
- Update the project version in pom files:
```bash
# replace x.x.x with release semantic version
$ mvn versions:set -DnewVersion=x.x.x
```
- Update CHANGELOG.md:
- Commit the changes:
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
