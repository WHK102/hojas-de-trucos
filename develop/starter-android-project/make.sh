#!/bin/bash

export ANDROID_CLI_ROOT='/media/whk/SecureData/Home/android/cli';
export ANDROID_SDK_ROOT='/media/whk/SecureData/Home/android/sdk';
export ANDROID_HOME="${ANDROID_SDK_ROOT}";
export CUSTOM_GRADLEW_WRAPPER_VERSION='5.6.4';
export CUSTOM_ANDROID_CLI_TOOLS_VERSION='6200805_latest';
export CUSTOM_BUILDTOOLS_BINARY_VERSION='29.0.3';
export CUSTOM_BUILDTOOLS_PACKAGE_VERSION='3.6.3';


cliToolsIntegrity(){
    if [ ! -f "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" ]; then

        echo '+ Installing the Android CLI tools ...';

        mkdir -p "${ANDROID_CLI_ROOT}/";
        mkdir -p "${ANDROID_SDK_ROOT}/";

        # Download
        wget \
            "https://dl.google.com/android/repository/commandlinetools-linux-${CUSTOM_ANDROID_CLI_TOOLS_VERSION}.zip" \
            -O "${ANDROID_CLI_ROOT}/data.zip";

        # Uncompress
        unzip -j "${ANDROID_CLI_ROOT}/data.zip" -d "${ANDROID_CLI_ROOT}/";

        # Update packages
        "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --update;
    fi
}

buildToolsIntegrity(){
    # Check the SDK manager integrity
    cliToolsIntegrity;

    # Last version:
    # "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --list | grep 'build-tools';
    # build-tools;29.0.3 | 29.0.3 | Android SDK Build-Tools 29.0.3
    echo '+ Validating build tools binary  ...';

    # Get last version
    # CUSTOM_BUILDTOOLS_BINARY_VERSION=$(
    #     "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --list | \
    #     grep -o 'build-tools;[0-9\.]\+ ' | \
    #     sort -r | \
    #     head -1 | \
    #     grep -o '[0-9\.]\+'
    # );
    echo "+ Using the Google Build tools binary version: ${CUSTOM_BUILDTOOLS_BINARY_VERSION}";

    "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" \
        --sdk_root="${ANDROID_SDK_ROOT}/" \
        --install "build-tools;${CUSTOM_BUILDTOOLS_BINARY_VERSION}";
}

showLastestDependencies(){
    # Check the SDK manager integrity
    cliToolsIntegrity;

    echo '+ Getting the last Build tools binary version ...';
    BUILDTOOLS_BINARY_VERSION=$(
        "${ANDROID_CLI_ROOT}/tools/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}/" --list | \
        grep -o 'build-tools;[0-9\.]\+ ' | \
        sort -r | \
        head -1 | \
        grep -o '[0-9\.]\+'
    );

    echo '+ Getting the last Build tools package version ...';
    BUILDTOOLS_PACKAGE_VERSION=$( \
        curl \
        -s 'https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google' | \
        grep -o ' release">\([0-9\.]\+\)\?<' | \
        head -1 | \
        grep -o '[0-9\.]\+' \
    );

    echo '+ Getting the last gradlew wrapper version ...';
    GRADLEW_WRAPPER_VERSION=$( \
        curl -s 'https://raw.githubusercontent.com/gradle/gradle/master/version.txt' \
    );

    echo '+ Getting the last Android Commandline Tools version ...';
    COMMANDLINETOOLS_VERSION=$( \
        curl -s 'https://developer.android.com/studio/index.html' | \
        grep -o 'commandlinetools-linux-[0-9a-z_]\+\.zip' | \
        head -1 | \
        grep -o '[0-9]\+' \
    );

    echo '+ Results:';
    echo "  -> Commandline Tools   : ${COMMANDLINETOOLS_VERSION}";
    echo "  -> Gradlew wrapper     : ${GRADLEW_WRAPPER_VERSION}";
    echo "  -> Build tools binary  : ${BUILDTOOLS_BINARY_VERSION}";
    echo "  -> Build tools package : ${BUILDTOOLS_PACKAGE_VERSION}";
}

initializeProject(){

    echo '+ Initializing project ...';

    # Validate the build tools integrity
    buildToolsIntegrity;

    echo '+ Making required folders ...';
    mkdir -p ./build/;
    mkdir -p ./app/build/;

    echo '+ Initializing gradlew ...';
    gradle init -Dorg.gradle.logging.level=quiet;

    echo '+ Upgrading gradlew wrapper ...';
    ./gradlew wrapper \
        --gradle-version="${CUSTOM_GRADLEW_WRAPPER_VERSION}" \
        --distribution-type=bin \
        -Dorg.gradle.logging.level=quiet;

    # Get the last version
    # CUSTOM_BUILDTOOLS_PACKAGE_VERSION=$( \
    #     curl \
    #     -s 'https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google' | \
    #     grep -o ' release">\([0-9\.]\+\)\?<' | \
    #     head -1 | \
    #     grep -o '[0-9\.]\+' \
    # );
    echo "+ Using the Google Build tools version for gradle from maven repository: ${CUSTOM_BUILDTOOLS_PACKAGE_VERSION}";
    echo '+ Transform the gradlew settings with the last version ...';

    # include the app project
    echo "
        include ':app'
    " > ./settings.gradle;

    echo "
        buildscript {

            repositories {
                google()
                jcenter()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:${CUSTOM_BUILDTOOLS_PACKAGE_VERSION}'
            }
        }

        allprojects {
            repositories {
                google()
                jcenter()
            }
        }

        task clean(type: Delete) {
            delete rootProject.buildDir
        }
    " > ./build.gradle;

    echo '+ Finished.';
}

compileProject(){

    # Project is initialized?
    if [ ! -f ./gradlew ]; then
        initializeProject;
    fi

    echo '+ Compilling project ...';
    ./gradlew build

    if [ $? -eq 0 ]; then
        echo '+ APK generated in:';
        ls -lah ./app/build/outputs/apk/debug/app-debug.apk;
        echo '+ Finished.';
    else
        echo '! Compile error.';
    fi
}

clearProject(){
    echo '+ Removing all unnecessary files ...';

    # Root files and folders
    rm -rf ./build;
    rm -rf ./gradle;
    rm -rf ./.gradle;
    rm -f ./build.gradle;
    rm -f ./gradlew;
    rm -f ./gradlew.bat;
    rm -f ./settings.gradle;

    # App files and folders
    rm -rf ./app/build;
    rm -rf ./app/.gradle;

    # System files and folders
    # rm -rf ~/.gradle;
    # rm -rf ~/.android;

    echo '+ Finished.';
}

printHelp(){
    echo 'Android Gradle Project Helper.';
    echo 'Use: ./make.sh [option]';
    echo 'Options:';
    echo '  init      Initialize the gradle wrapped project.';
    echo '  compile   Compile the gradlew project.';
    echo '  clear     Delete all unnecessary files.';
    echo '  lastest   Show lastest version of all dependencies.';
}

for argv in "$@"; do
    case "${argv}" in
        init)
            initializeProject;
            exit;
        ;;
        compile)
            compileProject;
            exit;
        ;;
        clear)
            clearProject;
            exit;
        ;;
        lastest)
            showLastestDependencies;
            exit;
        ;;
    esac
done

printHelp;