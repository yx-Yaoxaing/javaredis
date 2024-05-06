package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.datastruct.DataTypeEnum;
import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.helper.Command;
import com.cqnews.cloud.redis.store.rdb.RdbDiskStore;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DB {

    private static final int DEFAULT_DB_COUNT = 16;

    private ConcurrentHashMap<Integer,Store> dbMap;
    private RdbDiskStore rdbDiskStore;
    private  Store store;

    private ScheduledExecutorService executor;

    public DB() {
        this(DEFAULT_DB_COUNT);
    }

    public DB(int dbCount) {
        dbMap = new ConcurrentHashMap<>(dbCount);
        rdbDiskStore = new RdbDiskStore();
        executor = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < 1; i++) {
            store = new KVMemoryStore();
            dbMap.put(i,store);
            rdbDiskStore.rabLoad("D:\\code\\zjj",store.getDb());
            executor.scheduleAtFixedRate(()->{
                rdbDiskStore.rdbSave(store.getDb(),"D:\\code\\zjj");
            },3,5, TimeUnit.SECONDS);
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
            LinkedList<byte[]> list = new LinkedList<>();
            RedisObject cacheRedisObject = dbStore.get(key);
            if (cacheRedisObject != null) {
                list = (LinkedList<byte[]>) cacheRedisObject.getPtr();
            }
            if (command == Command.LPUSH) {
                for (String val : value) {
                    byte[] bytes = val.getBytes();
                    list.addFirst(bytes);
                }
                redisObject.setPtr(list);
            }
            redisObject.setPtr(list);
            dbStore.put(key.getBytes(),2000,redisObject);
        }
    }


    public byte[] get(String key,String filed,DataTypeEnum dataTypeEnum,Command command){
        Store dbStore = dbMap.get(0);
        if (command.getCommand().equals(Command.LPOP.getCommand())) {
            RedisObject cacheRedisObject = dbStore.get(key);
            if (cacheRedisObject == null) {
                return null;
            }
            LinkedList<byte[]> data = (LinkedList<byte[]>) cacheRedisObject.getPtr();
            if (data.size() == 0) {
                return null;
            }
            byte[] bytes = data.removeFirst();
            return bytes;
        }

        return null;
    }


}
