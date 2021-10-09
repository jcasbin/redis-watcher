Redis Watcher 
---
[![GitHub Actions](https://github.com/jcasbin/redis-watcher/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/jcasbin/redis-watcher/actions/workflows/maven-ci.yml)
![License](https://img.shields.io/github/license/jcasbin/redis-watcher)

Redis Watcher is a [Redis](http://redis.io) watcher for [jCasbin](https://github.com/casbin/jcasbin).

## Installation

**For Maven**

 ```
 <dependency>
 	<groupId>org.casbin</groupId>
     <artifactId>jcasbin-redis-watcher</artifactId>
     <version>1.0-SNAPSHOT</version>
 </dependency>
 ```

## Publish and Subscribe

Please learn redis [publish and subscribe](https://redisbook.readthedocs.io/en/latest/feature/pubsub.html) first.

Creating a ``redis-watcher`` will create a new thread for subscribing to the ``topic (Channel)`` of redis. Multiple instances create multiple watchers and corresponding threads subscribe to the same topic. When one of the instances executes the action of the update policy (such as ``e.addPolicy``, ``e.removePolicy`` ...), it will send a message to the topic, and then other instances that have subscribed to the topic will receive the notification and execute the ``updateCallback`` method. The default ``updateCallback`` is to call ``e.LoadPolicy``, which is the reload policy. (When you call ``e.setWatcher(redisWatcher)``, it will set default updateCallback)

## Simple Example

if you have two casbin instances A and B

**A:**  **Producer**

```java
String redisTopic="jcasbin-topic";
RedisWatcher redisWatcher = new RedisWatcher("127.0.0.1",6379, redisTopic);

Enforcer enforcer = new SyncedEnforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");
enforcer.setWatcher(redisWatcher);

// The following code is not necessary and generally does not need to be written unless you understand what you want to do
/*
Runnable updateCallback = ()->{
    // Custom behavior
};

redisWatcher.setUpdateCallback(updateCallback);
*/

// Modify policy, it will notify B
enforcer.addPolicy(...);
```

**B:** **Consumer**

````Java
String redisTopic="jcasbin-topic";
RedisWatcher redisWatcher = new RedisWatcher("127.0.0.1",6379, redisTopic);

Enforcer enforcer = new SyncedEnforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");
enforcer.setWatcher(redisWatcher);
// B set watcher and subscribe redisTopic, then it will receive the notification of A, and then call LoadPolicy to reload policy
````

## Getting Help

- [jCasbin](https://github.com/casbin/jCasbin)
- [jedis](https://github.com/redis/jedis)

## License

This project is under Apache 2.0 License. See the [LICENSE](https://github.com/jcasbin/redis-watcher/blob/master/LICENSE) file for the full license text.
