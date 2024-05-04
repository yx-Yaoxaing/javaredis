package com.cqnews.cloud.redis.datastruct;

public enum DataTypeEnum {
    REDIS_STRING(1,"REDIS_STRING"),
    REDIS_LIST(2,"REDIS_LIST")
    ;

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    private final int type;

    private final String desc;

    DataTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
