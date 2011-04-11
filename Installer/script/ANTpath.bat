powershell
[Environment]::SetEnvironmentVariable("ANT_HOME", "$INSTALL_PATH\ant\bin", "User")
[Environment]::SetEnvironmentVariable("JAVA_HOME", "$JAVA_HOME", "User")
[System.Environment]::SetEnvironmentVariable("PATH", $Env:Path + ";$INSTALL_PATH\ant\bin", "Machine")
