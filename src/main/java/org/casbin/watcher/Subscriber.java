package org.casbin.watcher;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
    private Runnable runnable;

    public Subscriber(Runnable updateCallback) {
        this.runnable = updateCallback;
    }

    public void setUpdateCallback(Runnable runnable){
        this.runnable = runnable;
    }

    public void onMessage(String channel, String message) {
        runnable.run();
    }
}
