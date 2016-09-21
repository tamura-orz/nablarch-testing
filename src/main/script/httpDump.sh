#!/bin/sh

cd `dirname $0`

CP=http-dump-1.0-jar-with-dependencies.jar
java -classpath ${CP} nablarch.test.core.http.dump.RequestDumpServer &

TMP_DIR=`dirname $1`/../dumptool

mkdir ${TMP_DIR}
TMP_FILE_NAME=`basename $1 .html`_temp.html
TMP_FILE=${TMP_DIR}/${TMP_FILE_NAME}


java -classpath ${CP} nablarch.test.core.http.dump.HtmlReplacerForRequestUnitTesting $1 ${TMP_FILE}

firefox ${TMP_FILE}