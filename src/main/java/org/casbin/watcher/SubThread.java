package org.casbin.watcher;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;

public class SubThread extends Thread{
    private final JedisPool jedisPool;
    private final Subscriber subscriber;
    private final String channel;

    public SubThread(JedisPool jedisPool,String channel,Runnable updateCallback) {
        super("SubThread");
        this.jedisPool = jedisPool;
        this.channel=channel;
        this.subscriber = new Subscriber(updateCallback);
    }
    public void setUpdateCallback(Runnable runnable){
        subscriber.setUpdateCallback(runnable);
    }

    public void setUpdateCallback(Consumer<String> consumer) {
        subscriber.setUpdateCallback(consumer);
    }

    @Override
    public void run() {
        while (true) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(subscriber, channel);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
