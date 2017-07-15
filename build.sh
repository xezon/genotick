#!/usr/bin/env bash
set -e

rm -Rf genotick-log-*.txt
rm -Rf savedPopulation_*
rm -Rf dist
mkdir dist
mvn clean package
export JAR=`ls target/genotick-*-jar-with-dependencies.jar`
cp ${JAR} dist/genotick.jar
java -jar ${JAR} input=default
cp -a data dist
cp LICENSE.txt dist
cp genotick-log-*.txt dist/genotick.log
cp -a savedPopulation_* dist/savedPopulation