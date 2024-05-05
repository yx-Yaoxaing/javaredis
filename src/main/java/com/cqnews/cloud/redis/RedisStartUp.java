package com.cqnews.cloud.redis;

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
        if (args.length == 0) {
            RedisServer redisServer = RedisServer.newInstance();
            redisServer.start(6379);
        }
    }

}
