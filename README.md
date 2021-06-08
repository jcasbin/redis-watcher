Redis Watcher 
---
[![GitHub Actions](https://github.com/jcasbin/redis-watcher/actions/workflows/maven-ci.yml/badge.svg)](https://github.com/jcasbin/redis-watcher/actions/workflows/maven-ci.yml)
![License](https://img.shields.io/github/license/jcasbin/redis-watcher)

Redis Watcher is a [Redis](http://redis.io) watcher for [jCasbin](https://github.com/casbin/jcasbin).

## Installation

For Maven

​    

## Simple Example	

```java
public static void main(String[] args) {
    // Initialize the watcher.
    // Use the Redis ip,port and topic name as parameter.
    String redisTopic="jcasbin-topic";
    RedisWatcher redisWatcher = new RedisWatcher("127.0.0.1",6379, redisTopic);

    // Initialize the enforcer.
    Enforcer enforcer = new Enforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");

    // Set the watcher for the enforcer.
    enforcer.setWatcher(redisWatcher);

    // Set callback to local example
    Runnable updateCallback = ()->{
        //do something
    };
    redisWatcher.setUpdateCallback(updateCallback);

    // Update the policy to test the effect.
    enforcer.savePolicy();
}
```

## Getting Help

- [jCasbin](https://github.com/casbin/jCasbin)
- [jedis](https://github.com/redis/jedis)

## License

This project is under Apache 2.0 License. See the [LICENSE](https://github.com/jcasbin/redis-watcher/blob/master/LICENSE) file for the full license text.
