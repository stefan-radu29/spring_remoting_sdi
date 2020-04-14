package config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JdbcConfig
{
    @Bean
    JdbcOperations jdbcOperations() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    DataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();


        basicDataSource.setUrl("jdbc:postgresql://localhost:5432/bookstore");
        basicDataSource.setUsername("postgres");
        basicDataSource.setPassword("parola");
        basicDataSource.setInitialSize(2);

        return basicDataSource;
    }
}
