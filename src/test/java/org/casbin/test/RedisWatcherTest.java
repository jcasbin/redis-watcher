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
    private RedisWatcher redisWatcher,redisConfigWatcher;
    private final String expect="update msg";
    private final String expectConfig="update msg for config";

    /**
     * You should replace the initWatcher() method's content with your own Redis instance.
     */
    @Before
    public void initWatcher(){
        String redisTopic = "jcasbin-topic";
        String redisConfig = "jcasbin-config";
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(2);
        config.setMaxWaitMillis(100 * 1000);
        redisWatcher = new RedisWatcher("127.0.0.1",6379, redisTopic, 2000, "foobared");
        redisConfigWatcher = new RedisWatcher(config,"127.0.0.1",6376, redisConfig, 2000, "foobaredConfig");
        Enforcer enforcer = new Enforcer();
        enforcer.setWatcher(redisWatcher);
        Enforcer configEnforcer = new Enforcer();
        configEnforcer.setWatcher(redisConfigWatcher);
    }

    @Test
    public void testConfigUpdate() throws InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        redisConfigWatcher.setUpdateCallback(()-> System.out.print(expectConfig) );
        redisConfigWatcher.update();
        Thread.sleep(100);
        Assert.assertEquals(expectConfig, expectConfig);
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

        redisConfigWatcher.setUpdateCallback((s) -> {
            System.out.print(s);
        });
        redisConfigWatcher.update();
        Thread.sleep(100);
    }

    @Test
    public void testConnectWatcherWithoutPassword() {
        String redisTopic = "jcasbin-topic";
        RedisWatcher redisWatcherWithoutPassword = new RedisWatcher("127.0.0.1", 6378, redisTopic);
        Assert.assertNotNull(redisWatcherWithoutPassword);

        String redisConfig = "jcasbin-config";
        RedisWatcher redisConfigWatcherWithoutPassword = new RedisWatcher("127.0.0.1", 6377, redisConfig);
        Assert.assertNotNull(redisConfigWatcherWithoutPassword);
    }
}
