#!/bin/bash

cd "$( dirname "$0" )"

PORT=27027
DIR=./target/test_mongo

which mongod > /dev/null || { echo "'mongod' is not in the search path. Is mongo installed?"; exit 1; }

function start() {
	mkdir -p $DIR/db
	mongod --port 27027 --logpath $DIR/log --logappend --pidfilepath $DIR/pid --dbpath $DIR/db --smallfiles --nssize 2 --noauth --nojournal --fork >/dev/null || { echo 'exiting'; exit 1; }
}

function stop() {
	mongo --port $PORT >/dev/null 2>&1 <<EOF
use admin
db.shutdownServer()
EOF
}

function running() {
	mongo --port $PORT >/dev/null 2>&1 <<EOF
exit
EOF
	return $?
}

function wait() {
	COUNT=0
	while true; do
		sleep 1
		running && break
		let COUNT=COUNT+1
		[[ $COUNT == 10 ]] && { echo "Server not started."; exit 1; }
		echo "Waiting for server..."
	done
}

case ${1:-start} in
start)
	( ! running ) && start
	wait
	;;
stop)
	stop
	;;
esac

