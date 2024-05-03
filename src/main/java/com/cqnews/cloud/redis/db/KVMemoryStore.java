package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.helper.Command;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class KVMemoryStore implements MemoryStore{

    @Override
    public boolean put(byte[] key, byte[] value, int expire, ConcurrentHashMap<String, Object> cache, Command command) {
        Cachekey cachekey = new Cachekey();
        cachekey.setKey(new String(key));
        cachekey.setCommand(command);
        cache.put(new String(key),value);
        return true;
    }
}
