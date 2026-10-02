#!/bin/sh
# Installs the library into a fresh local Maven repository, checks the jar's contents, then
# builds and runs the Java and Kotlin consumers against that repository on loopback.
set -eu

root=$(cd "$(dirname "$0")/.." && pwd)
version=$(sed -n 's:^  <version>\(.*\)</version>$:\1:p' "$root/pom.xml" | head -1)
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
repository="$work/m2"

(cd "$root" && mvn -B -q install -DskipTests -Dmaven.repo.local="$repository")
jar="$root/target/verimor-$version.jar"
unexpected=$(jar tf "$jar" | grep -vE '^(META-INF/|com/$|com/bariscemant/)' || true)
if [ -n "$unexpected" ]; then
  echo "Unexpected jar entries:" >&2
  echo "$unexpected" >&2
  exit 1
fi
mkdir "$work/jar"
(cd "$work/jar" && jar xf "$jar")
if grep -rq "verimor-sdk""-generator" "$work/jar"; then
  echo "The jar mentions the private generator" >&2
  exit 1
fi

for consumer in consumer-java consumer-kotlin; do
  cp -R "$root/tests/$consumer" "$work/$consumer"
  (cd "$work/$consumer" && mvn -B -q -Dmaven.repo.local="$repository" -Dverimor.version="$version" compile exec:java)
done

cp -R "$root/examples" "$work/examples"
(cd "$work/examples" && mvn -B -q -Dmaven.repo.local="$repository" -Dverimor.version="$version" compile)
echo "Examples compiled against the installed package."
