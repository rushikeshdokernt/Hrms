package com.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.auth.multitenancy.MultiTenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "defaultMasterDataSource")
    public DataSource defaultMasterDataSource() {
        return defaultDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "dataSource")
    public MultiTenantRoutingDataSource dataSource(@Qualifier("defaultMasterDataSource") DataSource defaultMasterDataSource) {
        return new MultiTenantRoutingDataSource(defaultMasterDataSource);
    }
}
