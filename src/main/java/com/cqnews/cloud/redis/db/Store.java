package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.helper.Command;

import java.util.concurrent.ConcurrentHashMap;

public interface Store {

    /**
     * 存储 k-v
     * @param key
     * @param value
     * @param expire
     * @return
     */
    boolean put(byte[] key,byte[] value,int expire, ConcurrentHashMap<String, Object> cache, Command command);


}
