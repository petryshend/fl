#!/bin/sh

mvn package
java -cp target/fl-1.0-SNAPSHOT-jar-with-dependencies.jar com.px.App
