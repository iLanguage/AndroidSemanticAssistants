echo Running Server
cd $INSTALL_PATH\Server

echo Creating new window to run server
START ant run

echo Disting CSAL
cd $INSTALL_PATH\CSAL
ant dist
