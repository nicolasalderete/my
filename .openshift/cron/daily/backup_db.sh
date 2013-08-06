#! /bin/sh
echo Borando archivos en $OPENSHIFT_REPO_DIR/../data/tmp/
rm $OPENSHIFT_REPO_DIR/../data/tmp/*
echo Comenzando con el backup de la base de datos
mysqldump -h $OPENSHIFT_MYSQL_DB_HOST -P $OPENSHIFT_MYSQL_DB_PORT -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD my > $OPENSHIFT_REPO_DIR/../data/tmp/shcema.sql
echo Enviando el mail a nicolasalderete@gmail.com
cat $OPENSHIFT_REPO_DIR/../data/tmp/shcema.sql | mail -s "BackUp" nicolasalderete@gmail.com
