#!/bin/bash

echo 'Cleaning ...';
rm -rf ../bin/*.jar
rm -rf ../app/build/

echo 'Compiling ...';
cd ../app/
./gradlew bootJar -x test

if [ $? -ne 0 ]; then
    echo 'The compilation has failed';
    exit -1;
fi

echo 'Publishing ...';
cp build/libs/*.jar ../bin/;
rm -rf build/

echo 'Compilation finished';
exit 0;