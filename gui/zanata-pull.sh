#!/bin/sh

mvn zanata:pull -Dzanta.projectType=utf8properties


find . -name "*.properties" | while read FILENAME; do native2ascii $FILENAME $FILENAME; done
