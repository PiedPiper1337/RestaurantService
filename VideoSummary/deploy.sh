#!/bin/bash
#note that this is only a backup copy of the file used to deploy on the server
sudo killall java
rm -rf videosummary-1.0-SNAPSHOT
unzip *.zip
rm *.zip
cp chromedriverLinux videosummary-1.0-SNAPSHOT/
cp chromedriverLinux videosummary-1.0-SNAPSHOT/bin
UP=$(pgrep Xvfb | wc -l);
if [ "$UP" -ne 1 ];
then
        echo "Xvfb is down";
        sudo nohup Xvfb :10 -ac &

else
        echo "Xvfb is already up";
fi


echo 'REMINDER THAT YOU STILL NEED TO EXPORT DISPLAY AND THE ACTUAL SERVER JAVA PROGRAM'
#two commands still need to be executed
echo 'export DISPLAY=:10'
echo 'sudo nohup ./videosummary -Dhttp.port=80 -Dplay.evolutions.db.default.autoApply=true -Dplay.evolutions.db.default.autoApplyDowns=true > application.log &'