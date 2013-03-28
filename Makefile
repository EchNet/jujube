# jujube project Makefile

all: clean build start

build: 
	mvn -q compile

clean:
	mvn -q clean

test:
	mvn -q clean test

.PHONY: all clean
