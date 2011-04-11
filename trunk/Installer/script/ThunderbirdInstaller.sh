echo "Generating Thunderbird XPI with CSAL.jar"
cd "$INSTALL_PATH/CSAL/dist"
cp -f CSAL.jar "$INSTALL_PATH/Clients/Mozilla/Thunderbird/SemanticAssistants@ca.ca"
cd "$INSTALL_PATH/Clients/Mozilla/Thunderbird/SemanticAssistants@ca.ca"
zip -r "Semantic AssistantTB.xpi" *

