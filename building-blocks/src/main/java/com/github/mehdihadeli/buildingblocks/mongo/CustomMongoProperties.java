package com.github.mehdihadeli.buildingblocks.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.mongodb")
public class CustomMongoProperties {
    private ConnectionPool connectionPool = new ConnectionPool();
    private Socket socket = new Socket();
    private boolean enabled = false;

    // Getters and Setters
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Nested class for connection pool settings
    public static class ConnectionPool {
        private int maxSize = 100;
        private int minSize = 10;

        // Getters and Setters
        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getMinSize() {
            return minSize;
        }

        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }
    }

    // Nested class for socket settings
    public static class Socket {
        private int connectTimeout = 5000;
        private int readTimeout = 5000;

        // Getters and Setters
        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
    }
}
