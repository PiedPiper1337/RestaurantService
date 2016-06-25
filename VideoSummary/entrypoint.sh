#!/usr/bin/env bash
#note that this is only a backup copy of the file used to deploy on the server
service mysql start
Xvfb :10 -ac &
cd /root/videosummary-1.0-SNAPSHOT/bin
./videosummary -Dhttp.port=80 -Dplay.evolutions.db.default.autoApply=true -Dplay.evolutions.db.default.autoApplyDowns=true