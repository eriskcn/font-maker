#!/usr/bin/env bash
set -e

# Build and deploy Android stub app via Gradle & ADB

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== 1. Checking Android SDK & ADB ==="
if ! command -v adb &> /dev/null; then
    if [ -n "$ANDROID_HOME" ] && [ -f "$ANDROID_HOME/platform-tools/adb" ]; then
        export PATH="$PATH:$ANDROID_HOME/platform-tools"
    elif [ -d "$HOME/Android/Sdk/platform-tools" ]; then
        export PATH="$PATH:$HOME/Android/Sdk/platform-tools"
    else
        echo "Error: adb command not found and no Android SDK detected in ANDROID_HOME or ~/Android/Sdk."
        exit 1
    fi
fi

# Detect SDK for local.properties if missing
if [ ! -f "local.properties" ]; then
    SDK_PATH="${ANDROID_HOME:-$HOME/Android/Sdk}"
    if [ -d "$SDK_PATH" ]; then
        echo "sdk.dir=$SDK_PATH" > local.properties
        echo "Generated local.properties with sdk.dir=$SDK_PATH"
    else
        echo "Warning: local.properties not found and cannot auto-detect SDK directory."
    fi
fi

echo "=== 2. Checking Gradle Wrapper ==="
if [ ! -f "./gradlew" ]; then
    if command -v gradle &> /dev/null; then
        echo "Generating Gradle wrapper..."
        gradle wrapper --gradle-version 8.4
    else
        echo "Error: ./gradlew not found and 'gradle' command is not installed."
        exit 1
    fi
fi
chmod +x ./gradlew

echo "=== 3. Building Debug APK ==="
./gradlew :app:assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK not found at $APK_PATH"
    exit 1
fi
echo "Build succeeded: $APK_PATH"

echo "=== 4. Checking Connected Android Device ==="
DEVICES=$(adb devices | grep -v "List of devices" | grep -w "device" || true)

if [ -z "$DEVICES" ]; then
    echo "No connected Android device/emulator found via adb."
    echo "APK is ready at: $APK_PATH"
    echo "Connect a device with USB debugging enabled, then run:"
    echo "  adb install -r $APK_PATH"
    echo "  adb shell am start -n com.fontmaker.app/.MainActivity"
    exit 0
fi

echo "Connected device detected. Installing APK..."
adb install -r "$APK_PATH"

echo "Launching application..."
adb shell am start -n com.fontmaker.app/.MainActivity

echo "=== Done! App launched on device. ==="
