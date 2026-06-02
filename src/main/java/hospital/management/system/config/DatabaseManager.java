package hospital.management.system.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager using HikariCP connection pool.
 * Replaces the old conn.java class which leaked connections.
 */
public final class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static HikariDataSource dataSource;

    private DatabaseManager() {
        // Prevent instantiation
    }

    /**
     * Initialize the connection pool. Must be called once at application startup.
     */
    public static synchronized void initialize() {
        if (dataSource != null) {
            logger.warn("DatabaseManager already initialized");
            return;
        }

        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));

        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "10000")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "300000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("db.pool.maxLifetime", "600000")));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        logger.info("Database connection pool initialized successfully");
    }

    /**
     * Get a connection from the pool. MUST be used with try-with-resources.
     *
     * @return a pooled database connection
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Call initialize() first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Shutdown the connection pool. Called at application exit.
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down");
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(is);
            logger.info("Database configuration loaded from db.properties");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
        return props;
    }
}
