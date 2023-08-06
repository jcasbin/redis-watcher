package org.casbin.test;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.watcher.lettuce.LettuceRedisWatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LettuceRedisWatcherTest {
    private LettuceRedisWatcher lettuceRedisWatcher;

    /**
     * You should replace the initWatcher() method's content with your own Redis instance.
     */
    @Before
    public void initWatcher() {
        String redisTopic = "jcasbin-topic";
        this.lettuceRedisWatcher = new LettuceRedisWatcher("127.0.0.1", 6379, redisTopic, 2000, null, "standalone");
        Enforcer enforcer = new Enforcer();
        enforcer.setWatcher(this.lettuceRedisWatcher);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        this.initWatcher();
        this.lettuceRedisWatcher.update();
        Thread.sleep(100);
    }

    @Test
    public void testConsumerCallback() throws InterruptedException {
        this.initWatcher();
        while (true) {
            this.lettuceRedisWatcher.setUpdateCallback((s) -> System.out.println(s));
            this.lettuceRedisWatcher.update();
            Thread.sleep(500);
        }
    }

    @Test
    public void testConnectWatcherWithoutPassword() {
        String redisTopic = "jcasbin-topic";
        LettuceRedisWatcher lettuceRedisWatcherWithoutPassword = new LettuceRedisWatcher("127.0.0.1", 6379, redisTopic, "standalone");
        Assert.assertNotNull(lettuceRedisWatcherWithoutPassword);
    }

    @Test
    public void testConnectWatcherWithType() {
        String redisTopic = "jcasbin-topic";
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            new LettuceRedisWatcher("127.0.0.1", 6379, redisTopic, "sentinel");
        });

        LettuceRedisWatcher lettuceRedisWatcherStandalone = new LettuceRedisWatcher("127.0.0.1", 6379, redisTopic, "standalone");
        Assert.assertNotNull(lettuceRedisWatcherStandalone);

        LettuceRedisWatcher lettuceRedisWatcherCluster = new LettuceRedisWatcher("127.0.0.1", 6379, redisTopic, "cluster");
        Assert.assertNotNull(lettuceRedisWatcherCluster);
    }
}