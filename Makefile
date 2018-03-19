.PHONY: build run fmt

SCALAFLAGS = -deprecation \
			 -Xfatal-warnings \
			 -Ywarn-unused \
			 -Ywarn-unused-import \
			 -Ywarn-dead-code \
			 -Ywarn-numeric-widen

build:
	@if [ ! -d build ]; then mkdir build; fi
	scalac $(SCALAFLAGS) -d build src/main/scala/*.scala

run:
	scala -classpath build xyz.minond.talk.pti.Main

fmt:
	scalafmt src/
