package com.cqnews.cloud.redis.server;

import com.cqnews.cloud.redis.actuator.Actuator;
import com.cqnews.cloud.redis.actuator.RedisActuator;
import com.cqnews.cloud.redis.commands.CommandParse;
import com.cqnews.cloud.redis.exception.ConstructorException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * redis server socket 处理 这里就是启动
 * 以及接受客户端的所有请求入口
 */
public class RedisServer {

    private final static AtomicBoolean startAtomic = new AtomicBoolean(false);

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private CommandParse commandParse;

    private Actuator actuator;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private RedisServer(){
        try {
            // 创建selector 和 通道channel
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            commandParse = new CommandParse();
            actuator = new RedisActuator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean start(int port) throws IOException {
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // 设为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 将ServerSocketChannel注册到Selector上，并指定监听Accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 阻塞直到有事件发生
            selector.select();
            // 获取所有发生的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove(); // 需要手动移除，否则会重复处理

                if (key.isAcceptable()) {
                    // 处理连接请求
                    handleAccept(key);
                } else if (key.isReadable()) {
                    // 处理读取数据
                    handleRead(key);
                }
            }
        }
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
        List<String> commandList = commandParse.parseBulkStrings(buffer);
        // 执行命令
        byte[] writeByteData = actuator.exec(commandList);


        socketChannel.write(ByteBuffer.wrap(writeByteData));
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
