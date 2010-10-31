#!/bin/bash

cd bin
java  -cp .:../../../CSAL/dist/CSAL.jar info.semanticsoftware.semassist.client.commandline.SACLClient $@
