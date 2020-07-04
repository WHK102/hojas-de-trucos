# Starter Android Project

Starter Android Project without IDE dependency.


## Requirements

- GNU/Linux (for bash helper).
- Gradle command (for debian based, can install usng `apt install gradle`).
- Text editor (Sublime Text 3 is recomended).


## Initialize enviroment

Set base directory

```bash
ANDROID_CLI_ROOT='/media/whk/SecureData/Home/android/cli';
ANDROID_SDK_ROOT='/media/whk/SecureData/Home/android/sdk';
```

Create folders

```bash
mkdir -p "${ANDROID_CLI_ROOT}/";
mkdir -p "${ANDROID_SDK_ROOT}/";
```


### Install Android CLI tools

From https://developer.android.com/studio/index.html#command-tools get the
version number (6200805) and download:

```bash
# Download
wget \
    https://dl.google.com/android/repository/commandlinetools-linux-6200805_latest.zip \
    -O "${ANDROID_CLI_ROOT}/data.zip";

# Uncompress
unzip -j "${ANDROID_CLI_ROOT}/data.zip" -d "${ANDROID_CLI_ROOT}/";

# Update packages
"${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --update;
```


### Install build tools

```bash
# Find the last version of build tools
"${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --list | grep 'build-tools';
# build-tools;29.0.3 | 29.0.3 | Android SDK Build-Tools 29.0.3

# Install the last version of build tools
"${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --install "build-tools;29.0.3";
```


## Initialize project

Use the helper bash script:

```bash
./make.sh init
```


## Compile project

Use the helper bash script:

```bash
./make.sh compile
```

## Notes

For base SDK version, by example, if you use `26` like as in
`./app/build.gradle`:

```gradle
android {
    compileSdkVersion 26
    defaultConfig {
        applicationId "com.packagename"
        minSdkVersion 26
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
    }
```

Need find the last stable version of `appcompat` from maven repository for `26`,
from https://mvnrepository.com/artifact/com.android.support/appcompat-v7 :

```gradle
implementation 'com.android.support:appcompat-v7:26.1.0'
```

## Example of use

```bash
$ ./make.sh 
Android Gradle Project Helper.
Use: ./make.sh [option]
Options:
  init      Initialize the gradle wrapped project.
  compile   Compile the gradlew project.
  clear     Delete all unnecessary files.
  lastest   Show lastest version of all dependencies.
$ ./make.sh lastest
+ Getting the last Build tools binary version ...
+ Getting the last Build tools package version ...
+ Getting the last gradlew wrapper version ...
+ Getting the last Android Commandline Tools version ...
+ Results:
  -> Commandline Tools   : 6200805
  -> Gradlew wrapper     : 6.6
  -> Build tools binary  : 29.0.3
  -> Build tools package : 3.6.3
$ ./make.sh compile
+ Initializing project ...
+ Validating build tools binary  ...
+ Using the Google Build tools binary version: 29.0.3
[=======================================] 100% Computing updates...             
+ Making required folders ...
+ Initializing gradlew ...
+ Upgrading gradlew wrapper ...
+ Using the Google Build tools version for gradle from maven repository: 3.6.3
+ Transform the gradlew settings with the last version ...
+ Finished.
+ Compilling project ...

BUILD SUCCESSFUL in 16s
50 actionable tasks: 50 executed
+ APK generated in:
-rw-rw-r-- 1 whk whk 1,5M may 26 18:58 ./app/build/outputs/apk/debug/app-debug.apk
+ Finished.
$ ./make.sh clear
+ Removing all unnecessary files ...
+ Finished.
```

Remember: You need edit the local SDK path for the downloads. No need download
the SDK manually.


## Sources

- https://github.com/WHK102/Automatic-Android-CLI-Compiler/blob/master/compile.sh
- https://stackoverflow.com/questions/37505709/how-do-i-download-the-android-sdk-without-downloading-android-studio
- https://developer.android.com/studio/index.html#command-tools
- https://developer.okta.com/blog/2018/08/10/basic-android-without-an-ide
