package com.cqnews.cloud.redis.commands;

import com.cqnews.cloud.redis.actuator.Actuator;
import com.cqnews.cloud.redis.actuator.RedisActuator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandExecutor {
    private final BlockingQueue<Command> commandQueue;
    private final ExecutorService executorService;
    private Actuator actuator;
    public CommandExecutor(int queueSize) {
        this.commandQueue = new LinkedBlockingQueue<>(queueSize);
        this.executorService = Executors.newSingleThreadExecutor();
        this.actuator = new RedisActuator();
        // 启动命令执行线程
        executorService.submit(() -> {
            while (true) {
                try {
                    Command command = commandQueue.take(); // 阻塞等待命令
                    executeCommand(command); // 执行命令
                } catch (InterruptedException e) {
                    //Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void executeCommand(Command command) {
        System.out.println("提交命令的线程名："+command.getSubmiTthreadName()+"执行任务的线程名称："+Thread.currentThread().getName());
        byte[] writeByteData = actuator.exec(command.getCommands());

        try {
            command.getSocketChannel().write(ByteBuffer.wrap(writeByteData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void enqueueCommand(Command command) throws InterruptedException {
        commandQueue.put(command);
    }

    // 关闭执行器，停止命令执行线程
    public void shutdown() {
        executorService.shutdown();
    }
}