package org.casbin.test;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.watcher.RedisWatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RedisWatcherTest {
    private RedisWatcher redisWatcher;
    private final String expect="update msg";

    @Before
    public void initWatcher(){
        String redisTopic = "jcasbin-topic";
        redisWatcher = new RedisWatcher("127.0.0.1",6379, redisTopic);
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
}
