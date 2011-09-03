#!/bin/bash

cd bin
# Accept embeded & quoted (not escaped) spaces in command-line arguments.
java  -cp .:../../../CSAL/dist/CSAL.jar info.semanticsoftware.semassist.client.commandline.SACLClient ${1+ "$@"}
