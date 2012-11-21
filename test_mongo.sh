#!/bin/bash

cd "$( dirname "$0" )"

PORT=27027
DIR=./target/test_mongo
mkdir -p $DIR

function start() {
	mongod --port $PORT --logpath $DIR/log --logappend --pidfilepath $DIR/pid --fork --dbpath $DIR/data--noauth --smallfiles || exit 1
}

function running() {
	return 1
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
	;;
esac

