package com.cqnews.cloud.redis.store.rdb;

/**
 * +-------+-------------+-----------+-----------------+-----+-----------+
 * | REDIS | RDB-VERSION | SELECT-DB | KEY-VALUE-PAIRS | EOF | CHECK-SUM |
 * +-------+-------------+-----------+-----------------+-----+-----------+
 *
 *                       |<-------- DB-DATA ---------->|
 */
public class RDB {

    /**
     * REDIS
     * 文件的最开头保存着 REDIS 五个字符，标识着一个 RDB 文件的开始。
     * 在读入文件的时候，程序可以通过检查一个文件的前五个字节，来快速地判断该文件是否有可能是 RDB 文件。
     */
    private String rdb = "REDIS";

    /**
     * RDB-VERSION
     * 一个四字节长的以字符表示的整数，记录了该文件所使用的 RDB 版本号。
     * 目前的 RDB 文件版本为 0006 。
     * 因为不同版本的 RDB 文件互不兼容，所以在读入程序时，需要根据版本来选择不同的读入方式。
     */
    private String rdbVersion = "0006";

    /**
     * 标志着数据库内容的结尾（不是文件的结尾），值为 rdb.h/EDIS_RDB_OPCODE_EOF （255）。
     */
    private String eof = "rdb.h/EDIS_RDB_OPCODE_EOF";


    /**
     * RDB 文件所有内容的校验和， 一个 uint_64t 类型值。
     * REDIS 在写入 RDB 文件时将校验和保存在 RDB 文件的末尾， 当读取时， 根据它的值对内容进行校验。
     * 如果这个域的值为 0 ， 那么表示 Redis 关闭了校验和功能。
     */
    private int checkSum = 0;

}
