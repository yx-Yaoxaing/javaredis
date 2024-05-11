package com.cqnews.cloud.redis.datastruct;

import java.util.*;

/**
 * 基座
 * 渐进式hashmap扩容的字典表
 * <p> Java原生自带的hashMap不适合作为存储机制 </p>
 * <li>1.扩容和缩容频繁</li>
 * <li>2.且扩容是同步的 如果key过多会导致扩容的时候 put流程阻塞
 * 边扩容边put就可以实现</li>
 */
public class Dict<K,V> implements Map<K,V> {

    // 加载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 容器容量
    private int capacity;
    // k-v 总个数
    private int size;
    // 存储容器 数组
    private Node<K, V>[] oldTable;
    // 为了渐进式扩容 拿来存储的
    private Node<K, V>[] newTable;

    // 是否正在进行扩容  默认false 代表没扩容
    private transient volatile boolean resizing = false;

    private transient int bucket = 0;

    public Dict(int capacity) {
        this.capacity = capacity;
        oldTable = new Node[capacity];
        newTable = null; // 初始时不需要新表
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // 检查是否需要扩容
    private boolean checkResize() {
        if (size >= capacity * DEFAULT_LOAD_FACTOR) {
            resize();
        }
        return true;
    }

    // 扩容
    private void resize() {
        // 代表正在扩容中
        if (resizing) {
            return;
        }
        int newCapacity = capacity * 2;
        newTable = new Node[newCapacity];
        // 标记扩容开始
        resizing = true;
        // 再接下来的 get put remove 都要进行一个桶的迁移 直到迁移桶的大小=oldTable的长度
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
        // 先去oldTable查询 如果查询不到 就去newTable查询
        int hash = hash(key);
        int oldTableIndex = (oldTable.length - 1) & hash;
        // oldTable
        Node<K, V> kvNode = get(hash, oldTable, oldTableIndex, key);
        if (kvNode == null && newTable != null) {
            // newTable
            int newTableIndex = (newTable.length - 1) & hash;
            kvNode = get(hash, newTable, newTableIndex, key);
        }
        // 如果正在扩容 就需要实现一个桶的迁移
        if (resizing) {
            if (bucket != oldTable.length) {
                Node<K, V> node = oldTable[bucket];
                while (node!=null) {
                    putNode(node.key,node.value,true);
                    node = node.next;
                }
                // 迁移桶成功后 需要将桶+1
                bucket++;
            } else {
                if (resizing) {
                    // 代表已经迁移完成
                    resizing = false;
                    bucket = 0;
                    oldTable = newTable;
                    newTable = null;
                }
            }
        }
        return kvNode == null ? null : kvNode.getValue();
    }


    private Node<K,V> get(int hash,Node<K,V> []table,int tableIndex,Object key){
        Node<K,V> entry = oldTable[tableIndex];
        if (entry == null) {
            return null;
        }
        while (entry!= null) {
            if (entry.hash == hash && (key.equals(entry.key))) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }


    private V putNode(K key, V value,boolean resizWirte) {
        // key 不能为null 作为redis的key 不能为空
        if (key == null) {
            throw new IllegalArgumentException("key dont not null");
        }

        // 判断是否需要扩容
        checkResize();

        int hash = hash(key);
        Node<K, V> newNode = new Node<>(hash, key, value, null);
        // 如果正在扩容 就直接写newTable
        if (resizing) {
            int newTableIndex = (newTable.length - 1) & hash;
            if (newTable[newTableIndex] != null) {
                // 采用头插法 这样新增的时候是O(1)
                newNode.next = newTable[newTableIndex];
            }
            newTable[newTableIndex] = newNode;
        } else{
            // 写oldTable
            int oldTableIndex = (oldTable.length - 1) & hash;
            if (oldTable[oldTableIndex] != null) {
                // 采用头插法 这样新增的时候是O(1)
                newNode.next = oldTable[oldTableIndex];
            }
            oldTable[oldTableIndex] = newNode;
        }
        if (!resizWirte) {
           size++;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        return this.putNode(key,value,false);
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

    /**
     * 这里我直接 copy 的jdkmap的hash计算方式
     * @param key
     * @return
     */
    static final int hash(Object key) {
        int h;
        return (h = key.hashCode()) ^ (h >>> 16);
    }


    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

}
