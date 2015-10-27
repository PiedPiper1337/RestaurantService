#!/bin/bash
UP=$(pgrep mysql | wc -l);
if [ "$UP" -ne 1 ];
then
        echo "MySQL is down. Starting up...";
        mysql.server start

else
        echo "MySQL is up.";
fi

if [ $# -eq 0 ]; then
    mysql -u root -e 'create database vidsum;'
    mysql -u root -e 'create user 'vidsummarizer'@'localhost' identified by "vidsummarizer";'
    mysql -u root -e 'GRANT CREATE ROUTINE, CREATE VIEW, ALTER, SHOW VIEW, CREATE, ALTER ROUTINE, EVENT, INSERT, SELECT, DELETE, TRIGGER, REFERENCES, UPDATE, DROP, EXECUTE, LOCK TABLES, CREATE TEMPORARY TABLES, INDEX ON vidsum.* TO 'vidsummarizer'@'localhost';'
else
    if [ $1 = "drop" ]; then
        mysql -u root -e 'drop database vidsum;'
        mysql -u root -e 'drop user vidsummarizer@localhost;'
    fi
fi

mysql.server stop

