package com.cqnews.cloud.redis.datastruct;


import java.io.Serializable;

public class RedisObject implements Serializable {


    // 使用的数据类型是什么
    // 如REDIS_STRING、REDIS_LIST、REDIS_SET、REDIS_ZSET、REDIS_HASH等，用于区分不同类型的值。
    private int type;

    private int encoding;

    // 实际数据结构的指针
    private Object ptr;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public Object getPtr() {
        return ptr;
    }

    public void setPtr(Object ptr) {
        this.ptr = ptr;
    }
}
