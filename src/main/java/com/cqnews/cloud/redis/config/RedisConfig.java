package com.cqnews.cloud.redis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RedisConfig {
    private String bindAddress;
    private int port;
    private int tcpBacklog;
    private int timeout;
    private int databases;
    private boolean rdbChecksum;
    private String dbFilename;
    private String dir;

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTcpBacklog() {
        return tcpBacklog;
    }

    public void setTcpBacklog(int tcpBacklog) {
        this.tcpBacklog = tcpBacklog;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDatabases() {
        return databases;
    }

    public void setDatabases(int databases) {
        this.databases = databases;
    }

    public boolean isRdbChecksum() {
        return rdbChecksum;
    }

    public void setRdbChecksum(boolean rdbChecksum) {
        this.rdbChecksum = rdbChecksum;
    }

    public String getDbFilename() {
        return dbFilename;
    }

    public void setDbFilename(String dbFilename) {
        this.dbFilename = dbFilename;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void loadFromConfigFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            // 去除注释和空白行
            if (line.startsWith("#") || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length < 2) {
                continue;
            }
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            switch (key) {
                case "bind":
                    this.bindAddress = value;
                    break;
                case "port":
                    this.port = Integer.parseInt(value);
                    break;
                case "dbfilename":
                    this.dbFilename = value;
                    break;
                case "dir":
                    this.dir = value;
                    break;
            }
        }
    }
}