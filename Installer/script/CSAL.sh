echo "CD to Server"
cd "$INSTALL_PATH/Server" -c
echo "Running the server"
ant run&
echo "Sleeping for 25 seconds, Please Wait......"
sleep 25
echo "CD to CSAL"
cd "$INSTALL_PATH/CSAL"
echo "Creating CSAL.jar"
ant dist
