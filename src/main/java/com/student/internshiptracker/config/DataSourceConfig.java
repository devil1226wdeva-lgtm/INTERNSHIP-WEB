package com.student.internshiptracker.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Attempting connection to MySQL at: {}", mysqlUrl);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
                log.info("=================================================");
                log.info("? Successfully connected to MySQL Server 8.0!");
                log.info("=================================================");
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(mysqlUrl);
                config.setUsername(mysqlUsername);
                config.setPassword(mysqlPassword);
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                return new HikariDataSource(config);
            }
        } catch (Exception ex) {
            log.warn("=================================================");
            log.warn("?? MySQL connection failed: {}", ex.getMessage());
            log.warn("Falling back to local persistent database (./data/internship_tracker) in MySQL mode.");
            log.warn("This allows the app to localhost immediately on http://localhost:8080 without downtime!");
            log.warn("To connect to MySQL, configure your password in application.properties or set $env:SPRING_DATASOURCE_PASSWORD.");
            log.warn("=================================================");

            HikariConfig fallbackConfig = new HikariConfig();
            fallbackConfig.setJdbcUrl("jdbc:h2:file:./data/internship_tracker;AUTO_SERVER=TRUE;MODE=MySQL;DB_CLOSE_DELAY=-1");
            fallbackConfig.setDriverClassName("org.h2.Driver");
            fallbackConfig.setUsername("sa");
            fallbackConfig.setPassword("");
            return new HikariDataSource(fallbackConfig);
        }
    }
}
