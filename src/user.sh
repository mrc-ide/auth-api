#!/usr/bin/env bash
./gradlew :userCLI:installDist
./userCLI/build/install/userCLI/bin/userCLI "$@"
