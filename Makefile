# hub project Makefile

all: clean build start

build: 
	mvn compile

clean:
	mvn clean

test: stop 
	mvn clean test

start: stop build 
	./start.sh

stop:
	mvn jetty:stop

.PHONY: all stop clean
