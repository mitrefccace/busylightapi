#!/bin/bash

# assumes that OpenFX SDK and OpenFX mods are installed
# assumes that the OPENFX_SDK and OPENFX_JMODS environment variables are defined. For example, for user mjones:
# OPENFX_SDK=/Users/mjones/javafx-sdk-11.0.2
# OPENFX_JMODS=/Users/mjones/javafx-jmods-11.0.2

java --module-path ${OPENFX_SDK}/lib --add-modules=javafx.controls -jar obusylight-1.0-SNAPSHOT-jar-with-dependencies.jar
