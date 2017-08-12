#!/usr/bin/env bash
set -e

rm -Rf genotick
mkdir genotick
mvn clean package
export JAR=`ls target/genotick-*-jar-with-dependencies.jar`
cp ${JAR} genotick/genotick.jar
cp -a data genotick
cp LICENSE.txt genotick
cp genotick-log-*.txt genotick/genotick.log
cp exampleConfigFile.txt genotick/
cp -a savedPopulation_* genotick/savedPopulation
rm -Rf genotick.zip
zip -r genotick.zip genotick
