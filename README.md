# Java Mobile App Template (Android)

This is a minimal Android mobile app template using Java and Gradle.

## Structure
- `app/src/main/java/` — Java source code
- `app/src/main/res/` — Resources (layouts, strings, etc.)
- `app/src/main/AndroidManifest.xml` — App manifest
- `build.gradle` — Project-level Gradle config
- `app/build.gradle` — App-level Gradle config

## How to Build
1. Install [Android Studio](https://developer.android.com/studio) or ensure you have the Android SDK and Gradle installed.
2. Open the project in Android Studio, or run:
   ```sh
   ./gradlew assembleDebug
   ```
3. The APK will be in `app/build/outputs/apk/debug/`.

## Minimum Requirements
- Java 8+
- Gradle 7+
- Android SDK 21+
