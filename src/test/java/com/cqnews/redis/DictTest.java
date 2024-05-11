package com.cqnews.redis;

import com.cqnews.cloud.redis.datastruct.Dict;
import com.cqnews.cloud.redis.datastruct.RedisObject;
import org.junit.Test;

import java.util.Map;

/**
 * 关于dict的测试
 */
public class DictTest {

    public static void main(String[] args) {
        Dict<String, Object> dict = new Dict<>(2);
        // 不扩容
        dict.put("1",1);
        // 不扩容
        dict.put("2",2);
        //
        dict.put("3",3);
        dict.get("2");
        dict.get("2");
        dict.get("2");
        dict.get("2");
        System.out.println("size="+dict.size());
    }

    @Test
    public void testEntrySet(){
        Dict<String, Object> dict = new Dict<>(2);
        // 不扩容
        dict.put("1",1);
        // 不扩容
        dict.put("2",2);
        dict.put("3",3);
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
    }




}
