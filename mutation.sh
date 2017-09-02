#!/bin/bash
set -e
mvn clean package
mvn org.pitest:pitest-maven:mutationCoverage

