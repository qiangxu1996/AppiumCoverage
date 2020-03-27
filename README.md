# AppiumCoverage
An Appium server for Android to generate JaCoCo coverage data

## Build

1. In `app/build.gradle`, change `TARGET_PACKAGE` in the following line to the package name of your app of interest

   ```groovy
   manifestPlaceholders = [targetPackage:"TARGET_PACKAGE"]
   ```

2. Either build in Android Studio, or run the following command

   ```shell
   ./graldew assembleDebug
   ```

3. You should be able to find the APK file at `app/build/outputs/apk/debug/app-debug.apk`

### Pre-Instrument Your App

For the Android instrumentation framework to collect coverage data, the app under test must be pre-instrumented before installed to the phone. A simple way is to add the following to your gradle script and execute the task `createDebugCoverageReport`.

```groovy
android {
  buildTypes {
    debug {
      testCoverageEnabled true
    }
  }
}
```

## Usage

Appium Coverage can be used either with Appium or standalone (through adb). For either approach

- I will assume both this app and the pre-instrumented target app have been installed on the phone
- Don't stop the app in the middle of the test
- After the test, the JaCoCo coverage file is at `/sdcard/Android/data/<your test package>/files/coverage.ec` on the phone

### Appium

Configure the following desired capabilities and perform the test as usual

- `appPackage`: the package ID of your target app
- `appActivity`: the activity that appears after app launch
- `androidCoverage`: `edu.purdue.dsnl.appiumcoverage/.CoverageInstrumentation`
- `androidCoverageEndIntent`: `edu.purdue.dsnl.appiumcoverage.END_EMMA`

### adb

1. `adb shell am instrument -w edu.purdue.dsnl.appiumcoverage/.CoverageInstrumentation`, and the app will automatically launch
2. Do what ever you want with the app, except stopping it
3. `adb shell am broadcast -a edu.purdue.dsnl.appiumcoverage.END_EMMA`

