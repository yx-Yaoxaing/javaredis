package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.datastruct.DataTypeEnum;
import com.cqnews.cloud.redis.datastruct.Dict;
import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.store.rdb.RdbDiskStore;

import java.util.concurrent.ConcurrentHashMap;

public class KVMemoryStore implements MemoryStore{

    private Dict<String, RedisObject> db;


    public KVMemoryStore() {
        db = new Dict<>(2);
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

    @Override
    public RedisObject get(String key) {
        return db.get(key);
    }

    @Override
    public Dict<String, RedisObject> getDb() {
        return db;
    }
}
