package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.datastruct.DataTypeEnum;
import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.helper.Command;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DB {

    private static final int DEFAULT_DB_COUNT = 16;

    private ConcurrentHashMap<Integer,Store> dbMap;

    private  Store store;

    public DB() {
        this(DEFAULT_DB_COUNT);
    }

    public DB(int dbCount) {
        dbMap = new ConcurrentHashMap<>(dbCount);
        for (int i = 0; i < dbCount; i++) {
            store = new KVMemoryStore();
            dbMap.put(i,store);
        }
    }

    public boolean doPutString(String key, byte[] value, DataTypeEnum dataTypeEnum){
        Store dbStore = dbMap.get(0);
        RedisObject redisObject = new RedisObject();
        redisObject.setType(dataTypeEnum.getType());
        redisObject.setPtr(value);
        dbStore.put(key.getBytes(),2000,redisObject);
        return true;
    }



    public byte[] get(String key, Command command){
        Store dbStore = dbMap.get(0);
        byte[] bytes = dbStore.get(key.getBytes());
        return bytes;
    }


    public void put(String key, DataTypeEnum dataTypeEnum, Command command,String ...value){
        Store dbStore = dbMap.get(0);

        RedisObject redisObject = new RedisObject();
        redisObject.setType(dataTypeEnum.getType());

        if (dataTypeEnum == DataTypeEnum.REDIS_LIST) {

        }

        redisObject.setPtr(value);


        dbStore.put(key.getBytes(),2000,redisObject);
    }

}
