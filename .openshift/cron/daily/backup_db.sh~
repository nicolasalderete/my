#! /bin/sh
NOW=$(date +"%m-%d-%Y")
echo Comenzando con el backup de la base de datos
mysqldump -h $OPENSHIFT_MYSQL_DB_HOST -P $OPENSHIFT_MYSQL_DB_PORT -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD my > $OPENSHIFT_REPO_DIR/../data/tmp/shcema.$NOW.log
echo Enviando el mail a nicolasalderete@gmail.com
cat $OPENSHIFT_REPO_DIR/../data/tmp/shcema.$NOW.sql | mail -s "BackUp" nicolasalderete@gmail.com
