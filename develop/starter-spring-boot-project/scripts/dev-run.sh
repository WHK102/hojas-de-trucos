#!/bin/bash

./compile.sh
if [ $? -ne 0 ]; then
    exit -1;
fi

echo 'Starting Spring application ...';
java -jar ../bin/*.jar;