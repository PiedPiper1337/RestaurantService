#!/bin/bash
#note that this is only a backup copy of the file used to deploy on the server
sudo killall java
rm -rf videosummary-1.0-SNAPSHOT
unzip *.zip
rm *.zip
#cd videosummary-1.0-SNAPSHOT/bin
#sudo nohup ./videosummary -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=80 > application.log &