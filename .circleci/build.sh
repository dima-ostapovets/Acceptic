#!/bin/bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DIR/.."

pushd "$PROJECT_DIR"
echo "run from build.sh build number" "$1"