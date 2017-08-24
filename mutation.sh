#!/bin/bash
mvn package
mvn org.pitest:pitest-maven:mutationCoverage

