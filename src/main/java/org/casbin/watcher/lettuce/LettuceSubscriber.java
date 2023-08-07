package org.casbin.watcher.lettuce;

import java.util.function.Consumer;

public class LettuceSubscriber {
    private Runnable runnable;
    private Consumer<String> consumer;

    public LettuceSubscriber(Runnable updateCallback) {
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
        if (this.consumer != null) {
            this.consumer.accept("Channel: " + channel + " Message: " + message);
        }
    }
}