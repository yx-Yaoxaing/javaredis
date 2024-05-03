package com.cqnews.cloud.redis.actuator;

import com.cqnews.cloud.redis.db.DB;
import com.cqnews.cloud.redis.helper.Command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;

/**
 * 执行器
 */
public class RedisActuator implements Actuator{

    // db层
    private DB db;
    // 存档任务指令
    private Queue queue;

    public RedisActuator() {
        // init db
        db = new DB();
    }


    @Override
    public byte[] exec(List<String> commands) {
        String s = commands.get(0);
        Command command = Command.parseCommand(s);
        byte[] okBytes = "+OK\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] notFoundBytes = "$-1\r\n".getBytes(StandardCharsets.UTF_8); // 假设用这个来表示key not found

        if (command == null) {
            return okBytes;
        }

        // strings
        if (command.getCommand().equals(Command.SET.getCommand())) {
            if (commands.size() >= 3) { // 确保有足够的参数
                System.out.println("commands:=" + commands);
                db.doPutString(commands.get(1), commands.get(2).getBytes(), command);
            }
            return okBytes;
        } else if (command.getCommand().equals(Command.GET.getCommand())) {
            if (commands.size() >= 2) {
                System.out.println("commands:=" + commands);
                byte[] value = db.get(commands.get(1), command);
                if (value != null) { // 假设db.get在找不到key时返回null
                    // Redis协议中字符串的响应格式是：$<length>\r\n<data>\r\n
                    int length = value.length;
                    byte[] lengthBytes = String.valueOf(length).getBytes(StandardCharsets.UTF_8);
                    byte[] response = new byte[1+1+2+length + 2 ]; // $、\r\n、<data>和\r\n的长度
                    System.arraycopy("$".getBytes(StandardCharsets.UTF_8), 0, response, 0, 1);
                    System.arraycopy(lengthBytes, 0, response, 1, lengthBytes.length);
                    System.arraycopy(("\r\n").getBytes(StandardCharsets.UTF_8), 0, response, 1 + lengthBytes.length, 2);
                    System.arraycopy(value, 0, response, 1 + lengthBytes.length + 2, value.length);
                    System.arraycopy(("\r\n").getBytes(StandardCharsets.UTF_8), 0, response, 1 + lengthBytes.length + 2 + value.length, 2);
                    return response;
                } else {
                    return notFoundBytes; // 如果key不存在，返回特殊的字节数组
                }
            }
        }
        // 可能需要处理其他命令或返回错误
        return okBytes; // 或者返回错误，取决于你的应用逻辑
    }
}
