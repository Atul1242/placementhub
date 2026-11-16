package com.placementhub.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Root application context: DataSource (HikariCP), Hibernate SessionFactory,
 * transaction manager and the password encoder. Scans the service and dao layers.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.placementhub.service", "com.placementhub.dao"})
@PropertySource("classpath:application.properties")
public class PersistenceConfig {

    private final Environment env;

    public PersistenceConfig(Environment env) {
        this.env = env;
    }

    /** Allow overriding DB settings via environment variables (useful for CI / Docker). */
    private String prop(String key, String envVar, String fallback) {
        String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return env.getProperty(key, fallback);
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(prop("jdbc.driverClassName", "DB_DRIVER", "com.mysql.cj.jdbc.Driver"));
        config.setJdbcUrl(prop("jdbc.url", "DB_URL",
                "jdbc:mysql://localhost:3306/placementhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"));
        config.setUsername(prop("jdbc.username", "DB_USER", "root"));
        config.setPassword(prop("jdbc.password", "DB_PASSWORD", ""));
        config.setMaximumPoolSize(10);
        config.setPoolName("PlacementHubPool");
        return new HikariDataSource(config);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.placementhub.entity");

        Properties props = new Properties();
        props.put(AvailableSettings.DIALECT,
                prop("hibernate.dialect", "DB_DIALECT", "org.hibernate.dialect.MySQLDialect"));
        props.put(AvailableSettings.HBM2DDL_AUTO,
                prop("hibernate.hbm2ddl.auto", "DB_DDL", "update"));
        props.put(AvailableSettings.SHOW_SQL,
                env.getProperty("hibernate.show_sql", "false"));
        props.put(AvailableSettings.FORMAT_SQL, "true");
        sessionFactory.setHibernateProperties(props);
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(LocalSessionFactoryBean sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory.getObject());
        return txManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
