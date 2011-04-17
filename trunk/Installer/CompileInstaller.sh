#!/bin/bash

cp ../SemassistProperties.xml varfile/
cp ../LICENSE.txt .
/usr/local/durmtools/IzPack/bin/compile install.xml -b . -o "Semantic Assistants Installer.jar" -k standard
