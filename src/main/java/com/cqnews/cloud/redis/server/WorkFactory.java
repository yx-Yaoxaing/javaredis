package com.cqnews.cloud.redis.server;

import com.cqnews.cloud.redis.commands.CommandExecutor;
import com.cqnews.cloud.redis.commands.CommandParse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkFactory implements Factory<Work>{

    private static List<Work> list = new ArrayList<>();

    private AtomicInteger counter = new AtomicInteger();

    @Override
    public Work poll() {
        int index = counter.getAndIncrement() % list.size();
        return list.get(index);
    }

    @Override
    public void create(String threadName, CommandExecutor commandExecutor, CommandParse commandParse) {
        list.add(new Work(threadName,commandExecutor,commandParse));
    }
}
