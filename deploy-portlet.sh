#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # this comment is required to trick cygwin into dealing with windows vs. linux EOL characters

./gradlew clean assemble
cp build/libs/PULSEAuthPortlet.war $PULSE_LIFERAY/deploy/
