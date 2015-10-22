#!/bin/bash
echo "===============Cleaning================"
activator clean
echo "===============Reloading==============="
activator reload
echo "===============Creating Zip============"
activator dist
echo "===============Copying to AWS============"
scp -i deployAmazon target/universal/videosummary-1.0-SNAPSHOT.zip deploy@aws:~

