package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.helper.Command;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DB {

    private static final int DEFAULT_DB_COUNT = 16;

    private ConcurrentHashMap<Integer,ConcurrentHashMap<String, Object>> dbMap;

    private  Store store;

    public DB() {
        this(DEFAULT_DB_COUNT);
    }

    public DB(int dbCount) {
        dbMap = new ConcurrentHashMap<>(dbCount);
        store = new KVMemoryStore();
        for (int i = 0; i < dbCount; i++) {
            dbMap.put(i,new ConcurrentHashMap<>(256));
        }
    }

    public boolean doPutString(String key, byte[] value, Command command){
        ConcurrentHashMap<String, Object> cache = dbMap.get(0);
        store.put(key.getBytes(),value,1000,cache,command);
        return true;
    }



    public byte[] get(String key, Command command){
        ConcurrentHashMap<String, Object> cache = dbMap.get(0);
        Object object = cache.get(key);
        return (byte[]) object;
    }

}
