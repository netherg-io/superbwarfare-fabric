#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
javac --release 21 -Xlint:all -Werror -d "$work" \
  src/main/java/com/atsuishio/superbwarfare/tools/DroneControlRules.java \
  dev/drone-control/DroneControlChecks.java
java -cp "$work" DroneControlChecks
