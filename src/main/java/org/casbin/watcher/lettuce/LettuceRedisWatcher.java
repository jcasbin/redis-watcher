package org.casbin.watcher.lettuce;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.lang3.StringUtils;
import org.casbin.jcasbin.persist.Watcher;
import org.casbin.watcher.lettuce.constants.WatcherConstant;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Consumer;

public class LettuceRedisWatcher implements Watcher {
    private final String localId;
    private final String redisChannelName;
    private final AbstractRedisClient abstractRedisClient;
    private LettuceSubThread lettuceSubThread;
    private Runnable updateCallback;

    /**
     * Constructor
     *
     * @param redisIp          Redis IP
     * @param redisPort        Redis Port
     * @param redisChannelName Redis Channel
     * @param timeout          Redis Timeout
     * @param password         Redis Password
     * @param type             Redis Type
     */
    public LettuceRedisWatcher(String redisIp, int redisPort, String redisChannelName, int timeout, String password, String type) {
        this.abstractRedisClient = this.getLettuceRedisClient(redisIp, redisPort, password, timeout, type);
        this.localId = UUID.randomUUID().toString();
        this.redisChannelName = redisChannelName;
        this.startSub();
    }

    /**
     * Constructor
     *
     * @param redisIp          Redis IP
     * @param redisPort        Redis Port
     * @param redisChannelName Redis Channel
     */
    public LettuceRedisWatcher(String redisIp, int redisPort, String redisChannelName, String type) {
        this(redisIp, redisPort, redisChannelName, 2000, null, type);
    }

    @Override
    public void setUpdateCallback(Runnable runnable) {
        this.updateCallback = runnable;
        lettuceSubThread.setUpdateCallback(runnable);
    }

    @Override
    public void setUpdateCallback(Consumer<String> consumer) {
        this.lettuceSubThread.setUpdateCallback(consumer);
    }

    @Override
    public void update() {
        try (StatefulRedisPubSubConnection<String, String> statefulRedisPubSubConnection =
                     this.getStatefulRedisPubSubConnection(this.abstractRedisClient)) {
            if (statefulRedisPubSubConnection.isOpen()) {
                String msg = "Casbin policy has a new version from redis watcher: ".concat(this.localId);
                statefulRedisPubSubConnection.async().publish(this.redisChannelName, msg);
            }
        }
    }

    private void startSub() {
        this.lettuceSubThread = new LettuceSubThread(this.abstractRedisClient, this.redisChannelName, this.updateCallback);
        this.lettuceSubThread.start();
    }

    /**
     * Initialize the Redis Client
     *
     * @param host     Redis Host
     * @param port     Redis Port
     * @param password Redis Password
     * @param timeout  Redis Timeout
     * @param type     Redis Type (standalone | cluster) default:standalone
     * @return AbstractRedisClient
     */
    private AbstractRedisClient getLettuceRedisClient(String host, int port, String password, int timeout, String type) {
        // todo default standalone ?
        // type = StringUtils.isEmpty(type) ? WatcherConstant.LETTUCE_REDIS_TYPE_STANDALONE : type;
        if (StringUtils.isNotEmpty(type) && StringUtils.equalsAnyIgnoreCase(type,
                WatcherConstant.LETTUCE_REDIS_TYPE_STANDALONE, WatcherConstant.LETTUCE_REDIS_TYPE_CLUSTER)) {
            RedisURI redisUri = null;
            if (StringUtils.isNotEmpty(password)) {
                redisUri = RedisURI.builder()
                        .withHost(host)
                        .withPort(port)
                        .withPassword(password.toCharArray())
                        .withTimeout(Duration.of(timeout, ChronoUnit.SECONDS))
                        .build();
            } else {
                redisUri = RedisURI.builder()
                        .withHost(host)
                        .withPort(port)
                        .withTimeout(Duration.of(timeout, ChronoUnit.SECONDS))
                        .build();
            }
            ClientResources clientResources = DefaultClientResources.builder()
                    .ioThreadPoolSize(4)
                    .computationThreadPoolSize(4)
                    .build();
            if (StringUtils.equalsIgnoreCase(type, WatcherConstant.LETTUCE_REDIS_TYPE_STANDALONE)) {
                // standalone
                ClientOptions clientOptions = ClientOptions.builder()
                        .autoReconnect(true)
                        .pingBeforeActivateConnection(true)
                        .build();
                RedisClient redisClient = RedisClient.create(clientResources, redisUri);
                redisClient.setOptions(clientOptions);
                return redisClient;
            } else {
                // cluster
                ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                        .autoReconnect(true)
                        .pingBeforeActivateConnection(true)
                        .validateClusterNodeMembership(true)
                        .build();
                RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources, redisUri);
                redisClusterClient.setOptions(clusterClientOptions);
                return redisClusterClient;
            }
        } else {
            throw new IllegalArgumentException("Redis-Type is required and can only be [standalone] or [cluster]");
        }
    }

    /**
     * Get Redis PubSub Connection
     *
     * @param abstractRedisClient Redis Client
     * @return StatefulRedisPubSubConnection
     */
    private StatefulRedisPubSubConnection<String, String> getStatefulRedisPubSubConnection(AbstractRedisClient abstractRedisClient) {
        if (abstractRedisClient instanceof RedisClient) {
            return ((RedisClient) abstractRedisClient).connectPubSub();
        } else {
            return ((RedisClusterClient) abstractRedisClient).connectPubSub();
        }
    }
}