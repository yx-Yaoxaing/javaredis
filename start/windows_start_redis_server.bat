@echo off
set JAVA_HOME=D:\code\jdk\jdk17
set PATH=%JAVA_HOME%\bin;%PATH%

java -cp "target" com.cqnews.cloud.redis.RedisStartUp redis.conf

pause