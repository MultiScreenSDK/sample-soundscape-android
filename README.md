# SoundScape

SoundScape is a demo app to demonstrate the how to integrate with MultiScreen-enabled Samsung
speakers using the MultiScreen Framework API. More information, including the application
communication protocol can be found [here](https://github.com/MultiScreenSDK/webapp-audioplayer/blob/master/soundscape.md).

## Building

The build requires [Gradle](http://www.gradle.org/downloads)
v1.10+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/path/to/your/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradlew` or `gradle assembleDebug` or `gradle assembleRelease` from the `app` directory to
  build the APK only
* Run one of the commands above from the root directory to build the app
* `gradle dist` will build the APK and copy it into the `dist` directory

## Building in Eclipse

Building in Eclipse requires the following dependencies:

* [EventBus](https://github.com/greenrobot/EventBus)
* [GSON](https://github.com/google/gson)
* [Picasso](https://github.com/square/picasso)
* [RippleEffect](https://github.com/traex/RippleEffect)
* [Material](https://github.com/rey5137/material)
* [AndroidSwipeLayout](https://github.com/daimajia/AndroidSwipeLayout)

RippleEffect, Material, and AndroidSwipeLayout, as well as, the dependent Android Extras need to be
imported as libraries into Eclipse. More information on how to import AARs can be found
[here](https://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html).
