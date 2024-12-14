#!/bin/sh
gradle :artio-initiator:shadowJar
gradle :media-driver:shadowJar
gradle :fix-engine:shadowJar

scp artio-initiator/build/libs/artio-initiator.jar artio@artio:/home/artio/apps
scp fix-engine/build/libs/fix-engine.jar artio@artio:/home/artio/apps
scp media-driver/build/libs/media-driver.jar artio@artio:/home/artio/apps