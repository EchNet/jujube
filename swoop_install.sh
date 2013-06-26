#!/bin/sh
SNAPSHOT_OR_RELEASE=${1:-release}
VERSION=${2:-0.1.3}
mvn deploy:deploy-file -DgroupId=net.ech -DartifactId=jujube -Dversion=$VERSION -DpomFile=pom.xml -Dpackaging=jar -Dfile=target/jujube-$VERSION.jar -Durl=dav:https://repository-swoop.forge.cloudbees.com/${SNAPSHOT_OR_RELEASE}/ -DrepositoryId=swoop-${SNAPSHOT_OR_RELEASE}
