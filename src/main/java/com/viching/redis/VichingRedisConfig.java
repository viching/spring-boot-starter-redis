package com.viching.redis;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.core.util.Duration;

/**
 * Configuration properties for Redis.（redis的属性配置类）
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class VichingRedisConfig {

    /**
     * Database index used by the connection factory.
     */
    private int database = 0;

    /**
     * Connection URL. Overrides host, port, and password. User is ignored. Example:
     * redis://user:password@example.com:6379
     */
    private String url;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Whether to enable SSL support.
     */
    private boolean ssl;

    /**
     * Connection timeout.
     */
    private int timeout;

    private Sentinel sentinel;

    private Cluster cluster;

    private final Jedis jedis = new Jedis();

    private final Lettuce lettuce = new Lettuce();

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Sentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Lettuce getLettuce() {
        return lettuce;
    }

    /**
     * Pool properties.（连接池的配置信息）
     */
    public static class Pool {

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private int maxWait = -1;
        /**
        *省略了关于连接池属性信息的get set方法
        */

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(int maxWait) {
            this.maxWait = maxWait;
        }
    }

    /**
     * Cluster properties.（集群配置信息）
     */
    public static class Cluster {

        /**
         * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
         * "initial" list of cluster nodes and is required to have at least one entry.
         */
        private List<String> nodes;

        /**
         * Maximum number of redirects to follow when executing commands across the
         * cluster.
         */
        private Integer maxRedirects;
        /**
        *省略了关于集群配置信息的get set方法
        */

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }
    }

    /**
     * Redis sentinel properties.（哨兵属性信息）
     */
    public static class Sentinel {

        /**
         * Name of the Redis server.
         */
        private String master;

        /**
         * Comma-separated list of "host:port" pairs.
         */
        private List<String> nodes;
        /**
        *省略了关于哨兵属性信息的get set方法
        */

        public String getMaster() {
            return master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

    }

    /**
     * Jedis client properties.（redis的客户端jedis）
     */
    public static class Jedis {

        /**
         * Jedis pool configuration.
         */
        private Pool pool;
        /**
        *省略了关于jedis属性信息的get set方法
        */

        public Pool getPool() {
            return pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }

    /**
     * Lettuce client properties.
     */
    public static class Lettuce {

        /**
         * Shutdown timeout.
         */
        private Duration shutdownTimeout = Duration.buildByMilliseconds(100f);

        /**
         * Lettuce pool configuration.
         */
        private Pool pool;

        public Duration getShutdownTimeout() {
            return shutdownTimeout;
        }

        public void setShutdownTimeout(Duration shutdownTimeout) {
            this.shutdownTimeout = shutdownTimeout;
        }

        public Pool getPool() {
            return pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }

}
