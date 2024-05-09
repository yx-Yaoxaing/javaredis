package com.cqnews.cloud.redis.store.rdb;

import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.store.RdbDisk;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rdb  https://redisbook.readthedocs.io/en/latest/internal/rdb.html
 */
public class RdbDiskStore implements RdbDisk {

    public static byte[] objectToByteArray(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
        return bos.toByteArray();
    }

    @Override
    public void rabLoad(String loadPath, ConcurrentHashMap<String, RedisObject> db) {
        if (loadPath == null || loadPath.isEmpty()) {
            return;
        }

        File rdbFile = new File(loadPath + "redis.rdb");
        if (!rdbFile.exists()) {
            System.err.println("RDB file does not exist: " + rdbFile.getAbsolutePath());
            return;
        }

        try (InputStream inputStream = new FileInputStream(rdbFile);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            // 读取 REDIS 标识符（可选，用于验证文件类型）
            byte[] magic = new byte[5];
            dataInputStream.readFully(magic);
            String magicString = new String(magic);
            if (!"REDIS".equals(magicString)) {
                System.err.println("Invalid RDB file magic block");
                return;
            }

            // 读取 RDB 版本号
            int version = dataInputStream.readInt();
            System.out.println("RDB version: " + Integer.toHexString(version));

            // 哪一个数据db 默认0-15 16个
            byte dbSelector = dataInputStream.readByte();

            while (dataInputStream.available() > 0) {
                String key = dataInputStream.readUTF();

                byte[] valueBytes = new byte[dataInputStream.available()];
                dataInputStream.readFully(valueBytes);

                RedisObject value = (RedisObject) byteArrayToObject(valueBytes);
                System.out.println("从磁盘上恢复数据:key=" + key + ",value=" + value);
                // 将键值对放入数据库中
                db.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rdbSave(ConcurrentHashMap<String, RedisObject> db, String savePath) {
        if (db == null || db.size() == 0) {
            return;
        }
        try (OutputStream outputStream = new FileOutputStream(savePath + "redis.rdb");
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            // 写入 REDIS 标识符
            dataOutputStream.writeBytes("REDIS");

            // 写入 RDB 版本号（假设为 0006）
            dataOutputStream.writeInt(Integer.parseInt("0006", 16)); // 将十六进制转换为整数

            dataOutputStream.writeByte(0);

            // 写入键值对数据
            for (Map.Entry<String, RedisObject> entry : db.entrySet()) {
                // 写入键
                dataOutputStream.writeUTF(entry.getKey());

                // 写入值（这里简化为字符串）

                dataOutputStream.write(objectToByteArray(entry.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public CompletableFuture<Integer> timeExec() {
        return CompletableFuture.supplyAsync(() -> {
            return 0;
        });
    }

    public static Object byteArrayToObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

}
