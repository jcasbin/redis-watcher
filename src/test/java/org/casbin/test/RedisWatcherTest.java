package org.casbin.test;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.watcher.RedisWatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RedisWatcherTest {
    private RedisWatcher redisWatcher;
    private final String expect="update msg";

    /**
     * You should replace the initWatcher() method's content with your own Redis instance.
     */
    @Before
    public void initWatcher(){
        String redisTopic = "jcasbin-topic";
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(2);
        redisWatcher = new RedisWatcher(config,"127.0.0.1",6379, redisTopic, 2000, "foobared");
        Enforcer enforcer = new Enforcer();
        enforcer.setWatcher(redisWatcher);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        redisWatcher.setUpdateCallback(()-> System.out.print(expect) );
        redisWatcher.update();
        Thread.sleep(100);
        Assert.assertEquals(expect, expect);
    }

    @Test
    public void testConsumerCallback() throws InterruptedException {
        redisWatcher.setUpdateCallback((s) -> {
            System.out.print(s);
        });
        redisWatcher.update();
        Thread.sleep(100);
    }

    @Test
    public void testConnectWatcherWithoutPassword() {
        String redisTopic = "jcasbin-topic";
        RedisWatcher redisWatcherWithoutPassword = new RedisWatcher("127.0.0.1", 6378, redisTopic);
        Assert.assertNotNull(redisWatcherWithoutPassword);
    }
}
