package com.cqnews.cloud.redis;

import com.cqnews.cloud.redis.config.RedisConfig;
import com.cqnews.cloud.redis.server.RedisServer;

import java.io.IOException;

/**
 * redis 启动入口
 *  1.激活server socket
 *  2.命令处理
 *  3.初始化db
 *  4.加载disk
 */
public class RedisStartUp {

    public static void main(String[] args) throws Exception {
        // Java命令行启动 如果没有指定配置文件 就加载resource
        RedisServer redisServer = RedisServer.newInstance();
        if (args.length == 0) {
            redisServer.start(6379);
        } else if (args.length == 1){
            String filePath = args[0];
            RedisConfig redisConfig = new RedisConfig();
            redisConfig.loadFromConfigFile(filePath);
            redisServer.start(redisConfig);
        } else {
            throw new RuntimeException("start redis server error");
        }
    }

}
