package com.cqnews.redis;

import com.cqnews.cloud.redis.datastruct.Dict;

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
    }


}
