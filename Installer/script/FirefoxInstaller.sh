echo "Generating Firefox XPI with CSAL.jar"
cd "$INSTALL_PATH/CSAL/dist"
cp -f CSAL.jar "$INSTALL_PATH/Clients/Mozilla/Firefox/SemanticAssistants@ca.ca/java"
cd "$INSTALL_PATH/Clients/Mozilla/Firefox/SemanticAssistants@ca.ca"
zip -r "Semantic AssistantFF.xpi" *
firefox "Semantic AssistantFF.xpi"
