# How to deploy on the nexus repository manager

The nexus repository manager only accepts lower case character in the module name and in the version
number.

All files must be signed with GPG.

```shell
gpg -ab *.jar
gpg -ab *.pom
```

```shell
ls -1
fastdoubleparser-0.5.0-javadoc.jar
fastdoubleparser-0.5.0-javadoc.jar.asc
fastdoubleparser-0.5.0-sources.jar
fastdoubleparser-0.5.0-sources.jar.asc
fastdoubleparser-0.5.0.jar
fastdoubleparser-0.5.0.jar.asc
fastdoubleparser.pom
fastdoubleparser.pom.asc
```