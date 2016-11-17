#!/bin/bash

#set -x

if [ "${PROJECTS_DIR}" == "" ] ; then
  PROJECTS_DIR="/home/tigase/projects"
fi

echo "Project home directory: ${PROJECTS_DIR}"

if [ "${1}" == "" ] ; then
	TEST_VERSION="master"
else
	TEST_VERSION="${1}"
fi

MY_DIR=`pwd`

cd ${PROJECTS_DIR}

#get or update sources
echo "Preparing server version: ${TEST_VERSION}"
if [ ! -e tigase-server ] ; then
	git clone https://repository.tigase.org/git/tigase-server.git
else
	cd tigase-server
	git fetch origin
	cd ..
fi

#prepare specific version
cd tigase-server
git reset --hard -q origin/master
git checkout -q -f ${TEST_VERSION}
cd ..

echo "Preparing tigase testsuite"
if [ ! -e tigase-testsuite ] ; then
        git clone https://repository.tigase.org/git/tigase-testsuite.git
else
		cd tigase-testsuite
        git pull
        git checkout master
		cd ..
fi

#clean previous builds
echo "Cleaning previous builds"
rm -rf tigase-server/pack/*
rm -rf tigase-server/target/*

#create packages
echo "Creating server package"
cd tigase-server
mvn -f modules/master/pom.xml  clean package
cd ..

if [ -e tigase-server-dist ] ; then
	rm -rf tigase-server-dist || exit -1
fi

mkdir tigase-server-dist || exit -1

tar --strip-components=1 -xf tigase-server/pack/tigase-server-*-dist-max.tar.gz -C tigase-server-dist

cd tigase-testsuite
mvn clean package

cd ${MY_DIR}