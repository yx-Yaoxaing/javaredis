package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.datastruct.DataTypeEnum;
import com.cqnews.cloud.redis.datastruct.RedisObject;

import java.util.concurrent.ConcurrentHashMap;

public class KVMemoryStore implements MemoryStore{
    ConcurrentHashMap<String, RedisObject> db ;
    public KVMemoryStore() {
        db = new ConcurrentHashMap<>(256);
    }


    @Override
    public boolean put(byte[] key, int expire, RedisObject redisObject) {
        db.put(new String(key),redisObject);
        return true;
    }

    @Override
    public byte[] get(byte[] key) {
        RedisObject redisObject = db.get(new String(key));

        if (redisObject.getType() == DataTypeEnum.REDIS_STRING.getType()) {
            return (byte[]) redisObject.getPtr();
        }
        return new byte[0];
    }
}
