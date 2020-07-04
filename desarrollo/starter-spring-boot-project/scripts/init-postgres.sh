#!/bin/bash


echo '+ Gettings permisions ...';

    sudo echo '' > /dev/null;


echo '+ Validating applications ...';

    which docker > /dev/null;
    if [ $? -ne 0 ]; then
        echo '! Docker is not installed. Required for Postgres instance.';
        exit -1;
    fi
    echo '[OK]';


echo '+ Validating docker instance ...';

    echo '  - Validating docker postgres 10 image ...';
        sudo docker images -q postgres:10 > /dev/null;
        if [ $? -ne 0 ]; then
            echo '  - Downloading postgres image ...';
            sudo docker pull postgres:10;
        else
            echo '  [OK]';
        fi

    echo '  - Validating Docker container ...';
        sudo docker ps -a | grep postgres10 > /dev/null;
        if [ $? -ne 0 ]; then
            echo '  - Creating postgres container ...';
            sudo docker run \
                -p 5432:5432 \
                --name postgres10 \
                -v /media/whk/SecureData/Data/Personal/Programación/Proyectos/WEB/certificados/dev/postgres10:/var/lib/postgresql/data \
                -e POSTGRES_PASSWORD=dev \
                -d postgres:10;
            sudo docker stop postgres10;
        else
            echo '  [OK]';
        fi


echo '+ Finished';