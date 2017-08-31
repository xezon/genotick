#!/bin/bash
set -e
mvn package
mvn org.pitest:pitest-maven:mutationCoverage

