package com.cqnews.cloud.redis.commands;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ResponseCommand {


    public static byte[] responsePing(){
        return "+PONG\r\n".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] responseOk(){
        return "+OK\r\n".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] responseErr(){
        return "$-1\r\n".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] responseValue(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        if (bytes == null || bytes.length == 0) {
            return "*0\r\n".getBytes(StandardCharsets.UTF_8); // 空列表
        }
        sb.append("$").append(bytes.length).append("\r\n");
        sb.append(new String(bytes)).append("\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] responseMGet(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "*0\r\n".getBytes(StandardCharsets.UTF_8); // 空列表
        }

        StringBuilder sb = new StringBuilder();

        // 先计算值的数量
        int count = 0;
        for (String value : values) {
            if (value != null) {
                count++;
            }
        }

        // 添加值的数量
        sb.append("*").append(count).append("\r\n");

        // 遍历并添加每个值
        for (String value : values) {
            if (value != null) {
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                sb.append("$").append(valueBytes.length).append("\r\n");
                sb.append(new String(valueBytes)).append("\r\n");
            } else {
                sb.append("$-1\r\n"); // 表示null值
            }
        }

        // 转换为byte数组
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
