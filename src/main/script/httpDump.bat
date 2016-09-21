@echo off

cd /d %~dp0

set LIB_DIR=../lib
set NTF_JAR=%LIB_DIR%/nablarch-tfw.jar
set POI_JAR=%LIB_DIR%//poi-3.2-FINAL-20081019.jar
set JETTY_JAR=%LIB_DIR%//jetty.jar;../lib/jetty-util.jar;
set SERVLET_JAR=%LIB_DIR%//servlet-api.jar
set CP=%NTF_JAR%;%POI_JAR%;%JETTY_JAR%;%SERVLET_JAR%
set JAVA_EXE=%JAVA_HOME%\bin\java\java.exe
start %JAVA_EXE% -classpath %CP% nablarch.test.core.http.dump.RequestDumpServer

set TMP_DIR=%~dp1\..\dumptool
mkdir %TMP_DIR%
set TMP_HTML=%TMP_DIR%\%~n1_temp.html
%JAVA_EXE% -classpath %CP% nablarch.test.core.http.dump.HtmlReplacerForRequestUnitTesting %1 %TMP_HTML%


start %TMP_HTML%
