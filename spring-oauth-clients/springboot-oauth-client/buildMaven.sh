#!/bin/bash
# Author: Rohtash Lakra
clear

# # Java Version
# JAVA_VERSION=21
# export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
# echo "JAVA_HOME: ${JAVA_HOME}"
# echo

VERSION="0.0"

# Build Version Function
function buildVersion() {
  GIT_COMMIT_COUNT=$(git rev-list HEAD --count)
  if [ $? -ne 0 ]; then
    VERSION="${VERSION}.1"
  else
    VERSION="${VERSION}.${GIT_COMMIT_COUNT}"
  fi
  SNAPSHOT="${SNAPSHOT:-$1}"
  if [[ ! -z ${SNAPSHOT} ]]; then
      VERSION="${VERSION}-SNAPSHOT"
  fi

  echo "${VERSION}"
}

SNAPSHOT_VERSION=$(buildVersion SNAPSHOT)
RELEASE_VERSION=$(buildVersion)
echo "RELEASE_VERSION: ${RELEASE_VERSION}, SNAPSHOT_VERSION: ${SNAPSHOT_VERSION}"
echo

# Build with Maven
mvn clean package -Drevision=$SNAPSHOT_VERSION -DskipTests
echo
