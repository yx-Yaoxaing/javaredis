package com.cqnews.cloud.redis.serialization;

import com.cqnews.cloud.redis.datastruct.RedisObject;

import java.io.*;

public class JdkSerialization implements Serialization<RedisObject> {
    @Override
    public RedisObject byteArrayToObject(byte[] bytes)  {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            ois.close();
            return (RedisObject) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] objectToByteArray(RedisObject obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
