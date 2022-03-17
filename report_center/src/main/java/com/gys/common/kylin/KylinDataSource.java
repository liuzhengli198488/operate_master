package com.gys.common.kylin;

import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;

@Slf4j
public class KylinDataSource implements DataSource {

    private LinkedList<Connection> connectionPoolList = new LinkedList<>();
    private long maxWaitTime;

    public KylinDataSource(KylinSqlProperties sqlProperties) {
        try {
            this.maxWaitTime = sqlProperties.getMaxWaitTime();
            Driver driverManager = (Driver) Class.forName(sqlProperties.getDriverClassName())
                    .newInstance();
            Properties info = new Properties();
            info.put("user", sqlProperties.getUserName());
            info.put("password", sqlProperties.getPassword());
            info.put("ssl",sqlProperties.getSSL());
            for (int i = 0; i < sqlProperties.getPoolSize(); i++) {
                Connection connection = driverManager
                        .connect(sqlProperties.getConnectionUrl(), info);
                connectionPoolList.add(ConnectionProxy.getProxy(connection, connectionPoolList));
            }
            log.info("PrestoDataSource has initialized {} size connection pool",
                    connectionPoolList.size());
        } catch (Exception e) {
            log.error("kylinDataSource initialize error, ex: ", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        synchronized (connectionPoolList) {
            if (connectionPoolList.size() <= 0) {
                try {
                    connectionPoolList.wait(maxWaitTime);
                } catch (InterruptedException e) {
                    throw new SQLException("getConnection timeout..." + e.getMessage());
                }
            }

            return connectionPoolList.removeFirst();
        }
    }


    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new RuntimeException("Unsupport operation.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new RuntimeException("Unsupport operation.");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new RuntimeException("Unsupport operation.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }


    static class ConnectionProxy implements InvocationHandler {

        private Object obj;
        private LinkedList<Connection> pool;
        private String DEFAULT_CLOSE_METHOD = "close";

        private ConnectionProxy(Object obj, LinkedList<Connection> pool) {
            this.obj = obj;
            this.pool = pool;
        }

        public static Connection getProxy(Object o, LinkedList<Connection> pool) {
            Object proxed = Proxy
                    .newProxyInstance(o.getClass().getClassLoader(), new Class[]{Connection.class},
                            new ConnectionProxy(o, pool));
            return (Connection) proxed;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals(DEFAULT_CLOSE_METHOD)) {
                synchronized (pool) {
                    pool.add((Connection) proxy);
                    pool.notify();
                }
                return null;
            } else {
                return method.invoke(obj, args);
            }
        }
    }
}
