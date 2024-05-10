#  基于Java实现Redis
## 前述
+ 关于redis作为缓存中间件是后端服务常见常用的缓存中间件，因为是基于C语言开源的，学了redis这么久就有了用golang或者Java实现一个redis的想法，但是网上已经有了golang版本的，所以我想做一个Java版本的redis.
+ 基本上是看着官网进行学习的<a>https://redis.io/docs redis,实现一个简易版本的redis 但是数据结构都会实现以及aof和rdb.

## 架构图
<img src="image/架构redis.png">
<img src="image/效果图.png">

## 简介
+ tcp服务没有采用netty实现，而是采用原生的NIO(全部实现)
+ redis是一个巨大的map，有着频繁扩容和缩容的操作，所以这里并不采用Java自带的HashMap，而是自定义实现的渐进式hash扩容Map
+ 常规数据类型与操作指令支持
  * string：get/mget/set/mset(全部实现)
  * list：lpush/lpop/rpush/rpop/lrange(lpush、lpop已实现)
  * set：sadd/sismember/srem(未完成)
  * hashmap：hset/hget/hdel(未完成)
  * sortedset：zadd/zremzrangebyscore (未完成)
+ 数据持久化机制
  * aof(未完成)
  * rdb(已开始未完成)
 
## 核心架构流程
<img src="image/redis设计流程图.png">

## redis理论知识
### 协议RESP
+ tcp服务器中通信客户端和服务端之前要有一个协议格式(这样服务端才能认识客户端给他发送的数据)，比如常见的http...一系列之类的但是http协议太过于大有一些对于redis来说是一种无效传输。
所以定义了一些协议格式叫RESP.https://redis.io/docs/latest/develop/reference/protocol-spec/#resp-protocol-description
我们主要是做的就是tcp传输数据后，我们拿到数据按照RESP协议来解析这个数据转换成命令即可。这一步最简单的方式就是看官方文档，看一遍不行就两遍。
个人理解就是二进制传输协议
### rdb和aof
+ <a>https://redisbook.readthedocs.io/en/latest/internal/rdb.html</a> 先看rdb的设计 然后理解它的设计，然后用代码实现即可。
### 主从复制
+ <a>https://redis.io/docs/latest/operate/oss_and_stack/management/replication/</a>
### 数据结构和数据类型
<a>https://redis.io/docs/latest/develop/data-types/</a>
#### 数据结构
+ list采用的双向链表(已实现)
+ dict采用的渐进式hash扩容(已实现)
+ sds没做采用的jdk String(已实现)
+ 跳表(未实现)