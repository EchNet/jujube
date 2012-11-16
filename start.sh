#!/bin/bash

cd "$( dirname "$0" )"

ARG=${1-dev}

# Argument determines listener port.
PORT=8001
if [ "$ARG" = "test" ] || [ "$ARG" = verify ] ; then
	PORT=8002
fi

mvn -Dhub.config=${CONFIG} -Dhub.port=${PORT} jetty:run > hub.out 2>&1 &

sleep 3

COUNT=0
while true; do
    curl "http://localhost:$PORT" >/dev/null 2>&1
    [[ $? == 0 ]] && break
    let COUNT=COUNT+1
    [[ $COUNT == 25 ]] && { echo "Server not started."; exit 1; }
    echo "Waiting for server..."
    sleep 1
done
curl localhost:$PORT
