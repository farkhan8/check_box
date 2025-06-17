#!/bin/bash

echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful, proceeding with installation..."
    
    # Get the package name from AndroidManifest.xml
    PACKAGE_NAME="com.example.radio"
    
    echo "Uninstalling previous version if exists..."
    adb uninstall $PACKAGE_NAME
    
    echo "Installing new version..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "Installation successful!"
    else
        echo "Installation failed!"
        exit 1
    fi
else
    echo "Build failed!"
    exit 1
fi
