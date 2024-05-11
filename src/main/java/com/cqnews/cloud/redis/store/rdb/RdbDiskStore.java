package com.cqnews.cloud.redis.store.rdb;

import com.cqnews.cloud.redis.datastruct.Dict;
import com.cqnews.cloud.redis.datastruct.RedisObject;
import com.cqnews.cloud.redis.serialization.JdkSerialization;
import com.cqnews.cloud.redis.serialization.Serialization;
import com.cqnews.cloud.redis.store.RdbDisk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * rdb  https://redisbook.readthedocs.io/en/latest/internal/rdb.html
 */

public class RdbDiskStore implements RdbDisk {

    private Logger log = LoggerFactory.getLogger(RdbDiskStore.class);

    private Serialization<RedisObject> serialization;

    private int lastTimeChangeTotal;

    public RdbDiskStore() {
        serialization = new JdkSerialization();
    }


    @Override
    public void rabLoad(String loadPath, Dict<String, RedisObject> db) {
        if (loadPath == null || loadPath.isEmpty()) {
            return;
        }

        File rdbFile = new File(loadPath + "redis.rdb");
        if (!rdbFile.exists()) {
            log.warn("RDB file does not exist: {}" ,rdbFile.getAbsolutePath());
            return;
        }

        try (InputStream inputStream = new FileInputStream(rdbFile);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            // 读取 REDIS 标识符（可选，用于验证文件类型）
            byte[] magic = new byte[5];
            dataInputStream.readFully(magic);
            String magicString = new String(magic);
            if (!"REDIS".equals(magicString)) {
                log.error("Invalid RDB file magic block");
                return;
            }

            // 读取 RDB 版本号
            int version = dataInputStream.readInt();

            // 哪一个数据db 默认0-15 16个 这里只实现了1个
            byte dbSelector = dataInputStream.readByte();

            AtomicInteger loadDiskToDbTotal = new AtomicInteger();
            while (dataInputStream.available() > 0) {
                int keyLength = dataInputStream.readInt();
                byte[] byteKey = new byte[keyLength];
                // 读取键
                dataInputStream.readFully(byteKey);
                String key = new String(byteKey);

                // 读取值的长度
                int valueLength = dataInputStream.readInt();
                byte[] byteValue = new byte[valueLength];
                // 读取值
                dataInputStream.readFully(byteValue);
                RedisObject value = serialization.byteArrayToObject(byteValue);
                // 将键值对放入数据库中
                db.put(key, value);
                loadDiskToDbTotal.getAndIncrement();
            }
            log.info("rdb load disk to db memory total: {}",loadDiskToDbTotal.get());
        } catch (IOException e) {
            log.error("rdb load disk to db memory error:{}",e.getMessage());
        }
    }

    @Override
    public void rdbSave(Dict<String, RedisObject> db, String savePath,int changeTotal) {
        if (db == null || db.isEmpty()) {
            log.info("rdb save disk,but changeTotal:{}",changeTotal);
            return;
        }

        if (lastTimeChangeTotal == 0 && changeTotal > 0) {
            doSave(db, savePath, changeTotal);
            log.info("rdb save disk,changeTotal:{}",changeTotal);
            return;
        }
        if (changeTotal - lastTimeChangeTotal > 5) {
            log.info("rdb save disk,changeTotal:{}",changeTotal);
            doSave(db, savePath, changeTotal);
        }
    }

    private void doSave(Dict<String, RedisObject> db, String savePath, int changeTotal) {
        try (OutputStream outputStream = new FileOutputStream(savePath + "redis.rdb");
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            // 1.写入 REDIS 标识符
            dataOutputStream.writeBytes("REDIS");
            // 2.写入 RDB 版本号（假设为 0006）
            dataOutputStream.writeInt(Integer.parseInt("0006", 16)); // 将十六进制转换为整数
            // 3.默认db 0
            dataOutputStream.writeByte(0);
            // 4.值
            // 写入键值对数据

            for (Map.Entry<String, RedisObject> entry : db.entrySet()) {
                String key = entry.getKey();
                byte[] byteKey = key.getBytes();
                byte[] byteValue = serialization.objectToByteArray(entry.getValue());
                // 写入键的长度
                dataOutputStream.writeInt(byteKey.length);
                // 写入键
                dataOutputStream.write(byteKey);
                // 写入值的长度
                dataOutputStream.writeInt(byteValue.length);
                // 写入值
                dataOutputStream.write(byteValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 更新变动次数
        lastTimeChangeTotal = changeTotal;
    }

    @Override
    public CompletableFuture<Integer> timeExec() {
        return CompletableFuture.supplyAsync(() -> {
            return 0;
        });
    }


}
