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
    SDK_CANDIDATES=(
        "$ANDROID_HOME"
        "$ANDROID_SDK_ROOT"
        "$HOME/Android/Sdk"
        "$HOME/.android/sdk"
        "/usr/lib/android-sdk"
        "/opt/android-sdk"
    )
    FOUND_SDK=""
    for candidate in "${SDK_CANDIDATES[@]}"; do
        if [ -n "$candidate" ] && [ -d "$candidate" ]; then
            FOUND_SDK="$candidate"
            break
        fi
    done

    if [ -n "$FOUND_SDK" ]; then
        echo "sdk.dir=$FOUND_SDK" > local.properties
        echo "Generated local.properties with sdk.dir=$FOUND_SDK"
    else
        echo "Error: Android SDK directory not found."
        echo "Set ANDROID_HOME or create local.properties manually:"
        echo "  echo 'sdk.dir=/path/to/android-sdk' > local.properties"
        exit 1
    fi
fi

echo "=== 2. Checking Gradle Wrapper ==="
mkdir -p gradle/wrapper
cat << 'EOF' > gradle/wrapper/gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF
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
