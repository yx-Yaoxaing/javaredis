package com.cqnews.cloud.redis.server;

public abstract class ServiceThread implements Runnable {

    public void start(){
        Thread thread = new Thread(this,getServiceName());
        thread.start();
    }

    public abstract String getServiceName();

    public abstract void read();

    @Override
    public void run() {
        read();
    }
}
