package com.example.demo.configs

import com.example.demo.props.ClickHouseProps
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.yandex.clickhouse.ClickHouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties
import java.sql.Connection

@Configuration
@EnableConfigurationProperties(ClickHouseProps::class)
class ClickHouseConfig(val properties: ClickHouseProps) {

    @Bean
    fun clickHouseConnection(): Connection {
        val props = ClickHouseProperties().apply {
            user = properties.username
            password = properties.password
        }

        return ClickHouseDataSource(properties.url, props).connection
    }
}