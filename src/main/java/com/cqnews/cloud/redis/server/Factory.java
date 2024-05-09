package com.cqnews.cloud.redis.server;

import com.cqnews.cloud.redis.commands.CommandExecutor;
import com.cqnews.cloud.redis.commands.CommandParse;

public interface Factory<T> {

    T poll();

    void create(String threadName, CommandExecutor commandExecutor, CommandParse commandParse);

}
