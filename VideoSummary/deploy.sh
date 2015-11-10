#!/bin/bash
#note that this is only a backup copy of the file used to deploy on the server
rm -rf videosummary-1.0-SNAPSHOT
unzip *.zip
rm *.zip
cd videosummary-1.0-SNAPSHOT/bin
sudo killall java
sudo nohup ./videosummary -Dhttp.port=80 > application.log &
