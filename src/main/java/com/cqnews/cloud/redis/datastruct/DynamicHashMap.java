package com.cqnews.cloud.redis.datastruct;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 基座
 * 渐进式hashmap扩容
 * <p> Java原生自带的hashMap不适合作为存储机制 </p>
 * <li>1.扩容和缩容频繁</li>
 * <li>2.且扩容是同步的 如果key过多会导致扩容的时候 put流程阻塞
 * 边扩容边put就可以实现</li>
 */
public class DynamicHashMap<K,V> implements Map<K,V> {

    private int size;


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
