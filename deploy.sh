#!/bin/sh
mvn deploy:deploy-file -DgroupId=net.ech -DartifactId=jujube -Dversion=0.1 -Dpackaging=jar -Dfile=target/jujube-0.1.jar -Durl=dav:https://repository-swoop.forge.cloudbees.com/release/ -DrepositoryId=swoop-release
