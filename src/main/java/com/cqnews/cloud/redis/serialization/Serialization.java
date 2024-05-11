package com.cqnews.cloud.redis.serialization;

import java.io.IOException;

public interface Serialization<K> {

    K byteArrayToObject(byte[] bytes);

    byte[] objectToByteArray(K object);

}
