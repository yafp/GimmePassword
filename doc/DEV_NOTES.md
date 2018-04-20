# GimmePassword - DevNotes

## General
### Icon Sizes
Android icons require five separate sizes for different screen pixel densities. Icons for lower resolution are created automatically from the baseline.

* mdpi (Baseline): 160 dpi 1×
* hdpi: 240 dpi 1.5×
* xhdpi: 320 dpi 2×
* xxhdpi: 480 dpi 3×
* xxxhdpi: 640 dpi 4× (launcher icon only)

#### Launcher icons (.Png)

*  48 × 48 (mdpi)
* 72 × 72 (hdpi)
* 96 × 96 (xhdpi)
* 144 × 144 (xxhdpi)
* 192 × 192 (xxxhdpi)
* 512 × 512 (Google Play store)

#### Action bar, Dialog & Tab icons

* 24 × 24 area in 32 × 32 (mdpi)
* 36 × 36 area in 48 × 48 (hdpi)
* 48 × 48 area in 64 × 64 (xhdpi)
* 72 × 72 area in 96 × 96 (xxhdpi)
* 96 × 96 area in 128 × 128 (xxxhdpi)*

#### Notification icons

* 22 × 22 area in 24 × 24 (mdpi)
* 33 × 33 area in 36 × 36 (hdpi)
* 44 × 44 area in 48 × 48 (xhdpi)
* 66 × 66 area in 72 × 72 (xxhdpi)
* 88 × 88 area in 96 × 96 (xxxhdpi)*

#### Small Contextual Icons

* 16 × 16 (mdpi)
* 24 × 24 (hdpi)
* 32 × 32 (xhdpi)
* 48 × 48 (xxhdpi)
* 64 × 64 (xxxhdpi)*


### Firebase
#### Getting Started
* https://firebase.google.com/docs/analytics/android/start/
#### Firebase Console
* https://console.firebase.google.com/?utm_source=studio


## Play Store
### Play Store Console
* https://play.google.com/apps/publish

### Generate feature graphic
* https://www.norio.be/android-feature-graphic-generator/




## Android-Studio

### Troubleshooting: cannot resolve symbol - out of nothing
It might help to
* close android-studio
* delete the folder  .idea/libraries
* restart android-studio

### Logging
* Log.e:
> This is for when bad stuff happens. Use this tag in places like inside a catch statement. You know that an error has occurred and therefore you're logging an error.

* Log.w:
> Use this when you suspect something shady is going on. You may not be completely in full on error mode, but maybe you recovered from some unexpected behavior. Basically, use this to log stuff you didn't expect to happen but isn't necessarily an error. Kind of like a "hey, this happened, and it's weird, we should look into it."

* Log.i:
> Use this to post useful information to the log. For example: that you have successfully connected to a server. Basically use it to report successes.

* Log.d:
> Use this for debugging purposes. If you want to print out a bunch of messages so you can log the exact flow of your program, use this. If you want to keep a log of variable values, use this.

* Log.v:
> Use this when you want to go absolutely nuts with your logging. If for some reason you've decided to log every little thing in a particular part of your app, use the Log.v tag.



### Lint
General informations about [lint](https://developer.android.com/studio/write/lint.html)

To run lint inside __android-studio__ jump to Terminal and execute
```
./gradlew lint
```

Output (XML & HTML) in ```\app\build\reports```

### Manually sync gradle
* Tools
* Android
* Sync Project With Gradle Files



## Unsorted
### Travis
General:
* https://docs.travis-ci.com/user/languages/android/

Useful template
* https://github.com/jaredsburrows/android-gradle-java-app-template/blob/master/.travis.yml


### Optional source for wordlists
* https://github.com/snowballstem/snowball-data
