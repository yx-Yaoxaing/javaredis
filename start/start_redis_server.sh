#!/bin/bash

# 检查参数数量
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 redis.conf"
    exit 1
fi

JAVA_CLASS="com.cqnews.cloud.redis.RedisStartUp"
REDIS_CONF_PATH=$1

java -cp "$CLASSPATH" "$JAVA_CLASS" "$REDIS_CONF_PATH"