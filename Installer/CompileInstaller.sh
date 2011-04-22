#! /bin/bash

# copy current SemassistProperties.xml and configure variables
cp ../SemassistProperties.xml varfile/
sed -i varfile/SemassistProperties.xml -e "s/\${durmtools}\/GATE\/gate/\$gate.location.dir/"
sed -i varfile/SemassistProperties.xml -e "s/\${durmtools}\/jaxws-ri/\$jaxws.location.dir/"

# copy current LICENSE text
cp ../LICENSE.txt .

# create installer
/usr/local/durmtools/IzPack/bin/compile install.xml -b . -o "SemanticAssistantsInstaller.jar" -k standard
