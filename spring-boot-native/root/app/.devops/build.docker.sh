#!/bin/bash

function cleanup {
  DIR=${PWD##*/}
  if [[ $DIR == ".devops" ]];
  then
    rm -f ../Dockerfile
  else
    rm -f Dockerfile
  fi

  if [[ "$FROM" != "$PWD" ]]; then
    popd >> /dev/null
  fi
}

trap cleanup exit

FROM=$PWD
DIR=${PWD##*/}

if [[ $DIR == ".devops" ]]; then
  pushd ../ >> /dev/null
fi

cp .devops/Dockerfile .
mvn clean package -Pshade -DskipTests -o
docker build . --tag=cmuramoto/letscode:app-1.0.0
