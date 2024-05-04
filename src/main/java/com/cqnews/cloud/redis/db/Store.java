package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.helper.Command;

import java.util.concurrent.ConcurrentHashMap;

public interface Store {

    /**
     * 存储 k-v
     * @param key
     * @param expire
     * @return
     */
    boolean put(byte[] key, int expire,RedisObject redisObject);

    byte[] get(byte[] key);


}
