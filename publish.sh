#!/bin/bash

echo "Building project social-publishing-gateway1.0"

name=social-publishing-gateway1.0
version=1.0

if [ "$1" != "" ]; then
	name=$1
fi
if [ "$2" != "" ]; then
	version=$2
fi

powertrain push NAME=$name VERSION=$version
