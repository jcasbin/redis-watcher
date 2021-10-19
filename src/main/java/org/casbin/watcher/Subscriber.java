package org.casbin.watcher;

import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;

public class Subscriber extends JedisPubSub {
    private Runnable runnable;
    private Consumer<String> consumer;

    public Subscriber(Runnable updateCallback) {
        this.runnable = updateCallback;
    }

    public void setUpdateCallback(Runnable runnable){
        this.runnable = runnable;
    }

    public void setUpdateCallback(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void onMessage(String channel, String message) {
        runnable.run();
        if (consumer != null)
            consumer.accept("Channel: " + channel + " Message: " + message);
    }
}
