package com.cqnews.cloud.redis.server;


import com.cqnews.cloud.redis.commands.Command;
import com.cqnews.cloud.redis.commands.CommandExecutor;
import com.cqnews.cloud.redis.commands.CommandParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象出来的工作线程模型
 * accept事件 由一个线程处理
 * work线程处理read事件 解析命令行
 */

public class Work extends ServiceThread {

    Logger log =  LoggerFactory.getLogger(Work.class);

    private final String threadName;
    private final CommandExecutor commandExecutor;
    private final CommandParse commandParse;
    private final Selector selector;

    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private AtomicBoolean start = new AtomicBoolean(false);

    public Work(String threadName,CommandExecutor commandExecutor,CommandParse commandParse) {
        log.info("read work init thread,threadName:{}",threadName);
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.threadName = threadName;
        this.commandExecutor = commandExecutor;
        this.commandParse = commandParse;
        // 启动线程
        start();
    }

    public void resiger(SocketChannel client){
        // 队列添加了一个read事件 这个read事件 需要由 work thread 取出read事件 然后执行
        queue.add(()->{
            try {
                client.register(selector,SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        });
        // 每次添加一个read事件的任务 就唤醒selector
        selector.wakeup();
    }


    @Override
    public String getServiceName() {
        return threadName;
    }

    @Override
    public void read() {
       for (;;){
           try {
               selector.select();
               Runnable readTask = queue.poll();
               if (readTask != null) {
                   readTask.run();
               }
               log.info("read event is triggered exec");
               Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
               while (keyIterator.hasNext()) {
                   // 当前事件 按道理下面的if 应该可以不判断 因为只有只有可读事件进来
                   SelectionKey key = keyIterator.next();
                   keyIterator.remove();
                   // work thread 只处理可读事件
                   if (key.isReadable()) {
                       SocketChannel client = (SocketChannel) key.channel();
                       ByteBuffer buffer = ByteBuffer.allocate(1024);
                       int bytesRead = -1;
                       try {
                           bytesRead = client.read(buffer);
                       } catch (Exception e) {
                           client.close();
                       }
                       // 当客户端退出的时候 会触发read事件 这里返回的是-1
                       if (bytesRead == -1) {
                           return;
                       }

                       buffer.flip();
                       byte[] bytes = new byte[buffer.remaining()];
                       buffer.get(bytes);
                       List<String> commandList = commandParse.parse(buffer);
                       log.info("parse redis command:{}",commandList);
                       // 执行命令
                       Command command = new Command();
                       command.setCommands(commandList);
                       command.setSubmitTime(System.currentTimeMillis());
                       command.setSubmiTthreadName(Thread.currentThread().getName());
                       command.setSocketChannel(client);
                       // 提交命令到执行器
                       commandExecutor.enqueueCommand(command);
                       // 清理缓冲区以准备下一次读取
                       buffer.clear();
                   }
               }
           } catch (IOException | InterruptedException e) {
               log.error("read event error:{}",e.getMessage());
               throw new RuntimeException(e);
           }
       }
    }




    public Selector getSelector() {
        return selector;
    }

    public AtomicBoolean getStart() {
        return start;
    }
}
