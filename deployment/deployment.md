# How to deploy to the nexus repository manager

The nexus repository manager only accepts lower case character in the module name and in the version
number.

We deploy the following files:

- fastdoubleparser/target/fastdoubleparser-x.y.z.jar
- fastdoubleparser/target/fastdoubleparser-x.y.z-sources.jar
- fastdoubleparser-java9/target/fastdoubleparser-java19-x.y.z-sources.jar


All files must be signed with GPG.

```shell
rm *.asc
for f in *.jar; do gpg -ab "$f"; done
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