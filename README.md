# SoundScape App

The SoundScape app

## HOW TO
Learn how to develop with IntelliJ and Gradle.

## Building

The build requires [Gradle](http://www.gradle.org/downloads)
v1.10+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/path/to/your/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradlew` or `gradle assembleDebug` or `gradle assembleRelease` from the `app` directory to build the APK only
* Run one of the commands above from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator.

You might find that your device doesn't let you install your build if you
already have the version from the Android Market installed.  This is standard
Android security as it it won't let you directly replace an app that's been
signed with a different key.  Manually uninstall SoundScape from your device and
you will then be able to install your own built version.

## Building in Eclipse

Why are you using Eclipse still? :)
Please use Android Studio, we do not support Eclipse.
