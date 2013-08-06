#! /bin/sh
mysqldump -h $OPENSHIFT_MYSQL_DB_HOST -P $OPENSHIFT_MYSQL_DB_PORT -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD my > app-root/data/tmp/shcema.sql
cat app-root/data/tmp/shcema.sql | mail -s "BackUp" nicolasalderete@gmail.com
