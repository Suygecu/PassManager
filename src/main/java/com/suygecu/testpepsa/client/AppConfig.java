package com.suygecu.testpepsa.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.suygecu.testpepsa.client")
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/task_manager");
        dataSource.setUsername("root");
        dataSource.setPassword("vell123009");
        System.out.println("Подключение к базе данных успешно");
        return  dataSource;
    }
}
