#!/bin/sh
VERSION=${1:-0.1.1}
mvn deploy:deploy-file -DgroupId=net.ech -DartifactId=jujube -Dversion=$VERSION -DpomFile=pom.xml -Dpackaging=jar -Dfile=target/jujube-$VERSION.jar -Durl=dav:https://repository-swoop.forge.cloudbees.com/release/ -DrepositoryId=swoop-release
