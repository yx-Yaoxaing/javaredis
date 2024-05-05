package com.cqnews.cloud.redis.server;

import com.cqnews.cloud.redis.actuator.Actuator;
import com.cqnews.cloud.redis.actuator.RedisActuator;
import com.cqnews.cloud.redis.commands.Command;
import com.cqnews.cloud.redis.commands.CommandExecutor;
import com.cqnews.cloud.redis.commands.CommandParse;
import com.cqnews.cloud.redis.config.RedisConfig;
import com.cqnews.cloud.redis.exception.ConstructorException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * redis server socket 处理 这里就是启动
 * 以及接受客户端的所有请求入口
 */
public class RedisServer {

    private final static AtomicBoolean startAtomic = new AtomicBoolean(false);

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private RedisConfig redisConfig;

    private CommandParse commandParse;

    private CommandExecutor commandExecutor;
    private Set<SocketChannel> channels;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    // 工作线程池
    private final ExecutorService workThreadPool =Executors.newFixedThreadPool(8);

    private RedisServer(){
        try {
            // 创建selector 和 通道channel
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            commandParse = new CommandParse();
            commandExecutor = new CommandExecutor(5000);
            channels = new HashSet<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean start(RedisConfig redisConfig) throws Exception{
        this.redisConfig = redisConfig;
        return this.start(redisConfig.getPort());
    }

    public boolean start(int port) throws Exception {
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // 设为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 将ServerSocketChannel注册到Selector上，并指定监听Accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;

            // 获取所有发生的事件
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                }

                keyIterator.remove();
            }
        }
    }



    private void read(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            int bytesRead = -1;
            try {
                bytesRead = client.read(buffer);
            } catch (Exception e) {
                channels.remove(client);
                client.close();
            }
            if (bytesRead == -1) {
                client.close();
                channels.remove(client);
                return;
            }

            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            List<String> commandList = commandParse.parse(buffer);
            // 执行命令
            Command command = new Command();
            command.setCommands(commandList);
            command.setSubmitTime(System.currentTimeMillis());
            command.setSubmiTthreadName(Thread.currentThread().getName());
            command.setSocketChannel(client);
            // 提交命令到执行器
            workThreadPool.submit(() -> {
                try {
                    commandExecutor.enqueueCommand(command);
                } catch (Exception e) {
                    // 处理或记录异常
                    e.printStackTrace();
                }
            });

            // 清理缓冲区以准备下一次读取
            buffer.clear();
        } catch (Exception e) {
            // 处理或记录异常
            e.printStackTrace();
            try {
                client.close();
            } catch (Exception ex) {
                // 忽略关闭异常
            }
            channels.remove(client);
        }
    }

    private void accept(SelectionKey key) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        channels.add(client);
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        // 注册读取事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Client connected: " + socketChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1) {
            // 客户端关闭连接
            socketChannel.close();
            System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
            return;
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        List<String> commandList = commandParse.parse(buffer);
        // 执行命令
        Command command = new Command();
        command.setCommands(commandList);
        command.setSubmitTime(System.currentTimeMillis());
        command.setSubmiTthreadName(Thread.currentThread().getName());
        command.setSocketChannel(socketChannel);

        // 解析的命令 交予  命令执行器 单线程模型
        try {
            commandExecutor.enqueueCommand(command);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static RedisServer newInstance(){
        if (startAtomic.compareAndSet(false,true)) {
            RedisServer redisServer = new RedisServer();
            return redisServer;
        } else {
            throw new ConstructorException("Redis socket server already start");
        }
    }


}
