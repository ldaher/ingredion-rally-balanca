@echo off
"%MULE_JAVA_HOME%\bin\java.exe" -XX:+UseParallelGC -Duser.language=es -Duser.country=ES -Dcom.sun.management.jmxremote -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006 -Xmx512m -Djava.class.path="%RALLY_HOME%\lib\rally.jar;%RALLY_HOME%\lib\opt.jar;%RALLY_HOME%\lib\mule.jar;" org.mule.MuleServer -config .\pdm-config.xml
