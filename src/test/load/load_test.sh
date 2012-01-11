#!/bin/sh

if [ ! -n "$HOST" ]; then
  HOST=127.0.0.1
fi

if [ -z $1 ]; then
  echo "Usage: [HOST=hostname] $0 file_to_upload"
  exit
fi
FILE_NAME=$1


function post {
    curl --data-binary @$1 http://$HOST:8080/fileserver/file/$1
}


while true; do time post $FILE_NAME; sleep 3; done