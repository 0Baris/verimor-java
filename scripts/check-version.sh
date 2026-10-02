#!/bin/sh
# Usage: scripts/check-version.sh <tag>   Fails unless the tag is v<project version from pom.xml>.
set -eu
root=$(cd "$(dirname "$0")/.." && pwd)
expected="v$(sed -n 's:^  <version>\(.*\)</version>$:\1:p' "$root/pom.xml" | head -1)"
if [ "$1" != "$expected" ]; then
  echo "Tag $1 does not match the pom version ($expected)" >&2
  exit 1
fi
