package com.cqnews.cloud.redis.store;

import com.cqnews.cloud.redis.datastruct.Dict;
import com.cqnews.cloud.redis.datastruct.RedisObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public interface RdbDisk {

    /**
     * 加载磁盘中的RDB文件到内存中 恢复成对象
     */
    void rabLoad(String loadPath, Dict<String, RedisObject> db);


    /**
     * 将内存中的数据对象持久化成RDB文件
     */
    void rdbSave(Dict<String, RedisObject> db, String savePath,int changeTotal);

    /**
     * 加载执行save 返回的个数为持久化多少个key
     * @return
     */
    CompletableFuture<Integer> timeExec();

}
