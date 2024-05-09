package com.cqnews.cloud.redis.actuator;

import com.cqnews.cloud.redis.commands.ResponseCommand;
import com.cqnews.cloud.redis.datastruct.DataTypeEnum;
import com.cqnews.cloud.redis.db.DB;
import com.cqnews.cloud.redis.helper.Command;
import com.cqnews.cloud.redis.server.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 执行器
 */
public class RedisActuator implements Actuator{
    Logger log =  LoggerFactory.getLogger(RedisActuator.class);
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
        if (command == null) {
            return ResponseCommand.responseOk();
        }

        // strings
        if (command.getCommand().equals(Command.SET.getCommand())) {
            if (commands.size() >= 3) {
                log.info("actuator string command - set");
                db.doPutString(commands.get(1), commands.get(2).getBytes(), DataTypeEnum.REDIS_STRING);
            }
            return ResponseCommand.responseOk();
        } else if (command.getCommand().equals(Command.GET.getCommand())) {
            if (commands.size() >= 2) {
                log.info("actuator string command - get");
                byte[] value = db.get(commands.get(1), command);
                if (value != null) { // 假设db.get在找不到key时返回null
                    // Redis协议中字符串的响应格式是：$<length>\r\n<data>\r\n
                    int length = value.length;
                    byte[] lengthBytes = String.valueOf(length).getBytes(StandardCharsets.UTF_8);
                    byte[] response = new byte[1+lengthBytes.length+2+length + 2 ]; // $、\r\n、<data>和\r\n的长度
                    System.arraycopy("$".getBytes(StandardCharsets.UTF_8), 0, response, 0, 1);
                    System.arraycopy(lengthBytes, 0, response, 1, lengthBytes.length);
                    System.arraycopy(("\r\n").getBytes(StandardCharsets.UTF_8), 0, response, 1 + lengthBytes.length, 2);
                    System.arraycopy(value, 0, response, 1 + lengthBytes.length + 2, value.length);
                    System.arraycopy(("\r\n").getBytes(StandardCharsets.UTF_8), 0, response, 1 + lengthBytes.length + 2 + value.length, 2);
                    return response;
                } else {
                    return ResponseCommand.responseErr(); // 如果key不存在，返回特殊的字节数组
                }
            }
        } else if (command.getCommand().equals(Command.MSET.getCommand())) {
            log.info("actuator string command - mset");
                //mset k1 v1 k2 v2
            if (commands.size() >= 3) {
                for (int i = 1; i < commands.size(); i += 2) {
                    String key = commands.get(i);
                    String value = commands.get(i + 1);
                    db.doPutString(key, value.getBytes(), DataTypeEnum.REDIS_STRING);
                }
            }
        } else if (command.getCommand().equals(Command.MGET.getCommand())) {
            log.info("actuator string command - mget");
            List<String> values = new ArrayList<>();
            for (int i = 1; i < commands.size(); i++) {
                String key = commands.get(i);
                byte[] valueBytes = db.get(key,command);
                if (valueBytes != null) {
                    values.add(new String(valueBytes));
                } else {
                    values.add(null);
                }
            }
            return ResponseCommand.responseMGet(values);
        }

        // list
        if (command.getCommand().equals(Command.LPUSH.getCommand())) {
            log.info("actuator list command - lpush");
            String key = commands.get(1);
            String[] array = new String[commands.size()-1-1];
            List<String> strings = Command.replaceValue(commands, 2, commands.size()-1);
            array = strings.toArray(array);
            db.put(key,DataTypeEnum.REDIS_LIST,command,array);
        } else if (command.getCommand().equals(Command.LPOP.getCommand())){
            log.info("actuator list command - lpop");
            String key = commands.get(1);
            byte[] bytes = db.get(key, null, DataTypeEnum.REDIS_LIST, command);
            return ResponseCommand.responseValue(bytes);
        }



        // ping pong
        if (command.getCommand().equals(Command.PING.getCommand())) {
            log.info("actuator PING command - PING");
            return ResponseCommand.responsePing();
        }
        return ResponseCommand.responseOk();
    }
}
