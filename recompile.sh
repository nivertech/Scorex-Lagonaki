#!/bin/sh

rm -rf target/

sbt clean

sbt compile

sbt assembly