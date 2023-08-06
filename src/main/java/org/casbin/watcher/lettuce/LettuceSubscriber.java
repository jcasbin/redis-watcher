package org.casbin.watcher.lettuce;

import java.util.function.Consumer;

public class LettuceSubscriber {
    private Consumer<String> consumer;

    public void setUpdateCallback(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void onMessage(String channel, String message) {
        if (this.consumer != null) {
            this.consumer.accept("Channel: " + channel + " Message: " + message);
        }
    }
}