#!/bin/bash
#Use this script to initialize the database for the first time
#use the command line argument "drop" to undo everything that this script does

UP=$(pgrep mysql | wc -l);
if [ "$UP" -ne 1 ];
then
    echo "MySQL is down. Starting up...";
    if [[ $OSTYPE == darwin* ]]
    then
        mysql.server start
    else
        service mysql start
    fi
else
    echo "MySQL is up.";
fi

if [ $# -eq 0 ]; then
    mysql -u root -e 'create database vidsum;'
    mysql -u root -e 'create user 'vidsummarizer'@'localhost' identified by "vidsummarizer";'
    mysql -u root -e 'GRANT CREATE ROUTINE, CREATE VIEW, ALTER, SHOW VIEW, CREATE, ALTER ROUTINE, EVENT, INSERT, SELECT, DELETE, TRIGGER, REFERENCES, UPDATE, DROP, EXECUTE, LOCK TABLES, CREATE TEMPORARY TABLES, INDEX ON vidsum.* TO 'vidsummarizer'@'localhost';'
    echo "Reminder that MySQL is currently running. "
    echo "Please remember to stop MySQL when you are finished with mysql.server stop"
else
    if [ $1 = "drop" ]; then
        mysql -u root -e 'drop database vidsum;'
        mysql -u root -e 'drop user vidsummarizer@localhost;'
        if [[ $OSTYPE == darwin* ]]
        then
            mysql.server stop
        else
            service mysql stop
        fi
    fi
fi
