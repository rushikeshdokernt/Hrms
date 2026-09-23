package com.employee.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.employee.multitenancy.MultiTenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * The default (master/fallback) DataSource built from spring.datasource.*
     * in application.properties. Used when no tenant is resolved.
     */
    @Bean(name = "defaultMasterDataSource")
    public DataSource defaultMasterDataSource() {
        return defaultDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * The @Primary DataSource bean — a MultiTenantRoutingDataSource that wraps
     * the master DataSource as default and dynamically routes to tenant-specific
     * HikariCP pools registered by TenantDataSourceManager.
     */
    @Primary
    @Bean(name = "dataSource")
    public MultiTenantRoutingDataSource dataSource(
            @Qualifier("defaultMasterDataSource") DataSource defaultMasterDataSource) {
        return new MultiTenantRoutingDataSource(defaultMasterDataSource);
    }
}
