#!/usr/bin/env bash
set -e

DIRNAME="$(cd "$(dirname "$0")" && pwd)"
GRADLE_VERSION="8.7"
GRADLE_DIR="$HOME/.gradle/custom_wrapper/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_DIR/bin/gradle"
ZIP_URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
ZIP_FILE="/tmp/gradle-$GRADLE_VERSION-bin.zip"

# Locate Java
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    export JAVA_HOME="$JAVA_HOME"
elif command -v java >/dev/null 2>&1; then
    # java in PATH
    :
else
    for jdk in /usr/lib/jvm/java-21-openjdk-amd64 /usr/lib/jvm/java-21-openjdk /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/default-java; do
        if [ -x "$jdk/bin/java" ]; then
            export JAVA_HOME="$jdk"
            export PATH="$JAVA_HOME/bin:$PATH"
            break
        fi
    done
fi

if ! command -v java >/dev/null 2>&1 && [ -z "$JAVA_HOME" ]; then
    echo "Error: Java 21/17 not found. Please install: sudo apt install openjdk-21-jdk" >&2
    exit 1
fi

# Bootstrap Gradle binary distribution if missing
if [ ! -x "$GRADLE_BIN" ]; then
    echo "Gradle $GRADLE_VERSION not found locally. Downloading..."
    mkdir -p "$(dirname "$GRADLE_DIR")"

    if command -v curl >/dev/null 2>&1; then
        curl -sSL -o "$ZIP_FILE" "$ZIP_URL"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$ZIP_FILE" "$ZIP_URL"
    else
        echo "Error: curl or wget is required." >&2
        exit 1
    fi

    if ! command -v unzip >/dev/null 2>&1; then
        echo "Error: unzip is required. Run: sudo apt install unzip" >&2
        exit 1
    fi

    unzip -q -o "$ZIP_FILE" -d "$(dirname "$GRADLE_DIR")"
    rm -f "$ZIP_FILE"
    chmod +x "$GRADLE_BIN"
    echo "Gradle $GRADLE_VERSION installed to $(dirname "$GRADLE_DIR")"
fi

exec "$GRADLE_BIN" "$@"
