package com.cqnews.cloud.redis.commands;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * 命令解析 RESP协议说明 RESP2
 * <a>https://redis.io/docs/latest/develop/reference/protocol-spec/#resp-protocol-description</a>
 */
public class CommandParse {

    /**
     * *3
     * $3
     * set
     * $3
     * ky1
     * $2
     * v2
     */
    public Object parse(String respString) {

        // 在 RESP 中，数据的第一个字节决定其类型。
        // ( CRLF \r\n) 是协议的终止符，它总是分隔各个部分。
        char[] charArray = respString.toCharArray();
        

        return null;
    }

    /**
     *  第一个字节是 $ 代表是二进制安全的字符串
     *  $3 代表后续读三个长度的字符串
     *  * $3
     *  * set
     *
     * @param resp
     */
    public void parse(String resp,boolean res){
        char[] charArray = resp.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char index = charArray[i];
            switch (index) {
                case '*' :
                    break;
                case '$' :
                   // parseBulkStrings(resp);
                case '+' :
                    break;
            }
        }
    }

    public List<String> parseBulkStrings(ByteBuffer buffer) {
        // 切换读模式
        buffer.flip();
        buffer.get(); // 跳过 '*'
        // 读取数组长度
        int arrayLength = 0;
        while (buffer.hasRemaining() && Character.isDigit((char) buffer.get(buffer.position()))) {
            arrayLength = arrayLength * 10 + (buffer.get() - '0');
        }
        // 跳过 CRLF
        buffer.get(); // '\r'
        buffer.get(); // '\n'

        // 遍历数组
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arrayLength; i++) {
            char type = (char) buffer.get();
            // 二进制安全
            if (type == '$') {
                int dataLength = 0;
                // 读取数据长度，直到遇到 CRLF
                while (buffer.hasRemaining() && Character.isDigit((char) buffer.get(buffer.position()))) {
                    dataLength = dataLength * 10 + (buffer.get() - '0');
                }
                // 跳过 CRLF
                buffer.get(); // '\r'
                buffer.get(); // '\n'

                byte[] data = new byte[dataLength];
                buffer.get(data); // 读取数据

                // 跳过 CRLF
                buffer.get(); // '\r'
                buffer.get(); // '\n'

                String dataString = new String(data, StandardCharsets.UTF_8);
                list.add(dataString);
            } else if (type == '+') {
                // 处理简单字符串（这里只是跳过它）
                // ...
            } else if (type == '-') {
                // 处理错误（这里只是跳过它）
                // ...
            } else {
                // 不识别的类型
                throw new IllegalStateException("Unrecognized RESP type: " + type);
            }
    }
        return list;
  }
}
