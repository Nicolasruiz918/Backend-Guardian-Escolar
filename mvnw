#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

docker run --rm \
  -v "$SCRIPT_DIR":/workspace \
  -v gps_guardian_escolar_m2:/root/.m2 \
  -w /workspace \
  maven:3.9.11-eclipse-temurin-17 \
  mvn "$@"
