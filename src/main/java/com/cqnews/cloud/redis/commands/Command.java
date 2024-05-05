package com.cqnews.cloud.redis.commands;

import java.nio.channels.SocketChannel;
import java.util.List;

public class Command {

    private Long submitTime;

    private List<String> commands;

    private String submiTthreadName;

    private SocketChannel socketChannel;

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public String getSubmiTthreadName() {
        return submiTthreadName;
    }

    public void setSubmiTthreadName(String submiTthreadName) {
        this.submiTthreadName = submiTthreadName;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
