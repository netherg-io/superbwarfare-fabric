#!/usr/bin/env bash
set -euo pipefail
root=$(cd "$(dirname "$0")/../.." && pwd)
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
kotlinc -Werror -jvm-target "${KOTLIN_JVM_TARGET:-21}" \
  "$root/src/main/kotlin/com/atsuishio/superbwarfare/network/security/DroneControlPolicy.kt" \
  "$root/tools/drone-tests/DroneControlPolicyTest.kt" -include-runtime -d "$work/contracts.jar"
java -jar "$work/contracts.jar"
