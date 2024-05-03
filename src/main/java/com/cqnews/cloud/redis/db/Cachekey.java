package com.cqnews.cloud.redis.db;

import com.cqnews.cloud.redis.helper.Command;

public class Cachekey {

    private String key;

    private Command command;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
