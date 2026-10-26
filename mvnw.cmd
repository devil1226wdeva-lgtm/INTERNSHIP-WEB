@echo off
set "JAVA_HOME=C:\Users\devan\AppData\Roaming\.minecraft\runtime\java-runtime-delta\windows\java-runtime-delta"
set "MAVEN_HOME=%~dp0.tools\apache-maven-3.9.6"
set "PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%"
set "MAVEN_OPTS=-XX:+EnableDynamicAgentLoading -javaagent:C:\Users\devan\.m2\repository\net\bytebuddy\byte-buddy-agent\1.14.13\byte-buddy-agent-1.14.13.jar"
"%MAVEN_HOME%\bin\mvn.cmd" %*
