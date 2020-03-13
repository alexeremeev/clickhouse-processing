package com.example.demo.configs

import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.MergedContextConfiguration
import org.testcontainers.containers.ClickHouseContainer

class TestConfig {

    companion object {
        const val TEST_USER = "test_user_1"
    }

    class CustomContextLoader : SpringBootContextLoader() {

        companion object {
            private val clickhouse = ClickHouseContainer("yandex/clickhouse-server:19.15")
                .withInitScript("schema.sql")

            init {
                clickhouse.start()
                Runtime.getRuntime().addShutdownHook(Thread { clickhouse.stop() })
            }
        }

        override fun getInlinedProperties(config: MergedContextConfiguration) =
            super.getInlinedProperties(config) +
                    arrayOf(
                        "app.clickhouse.url=${clickhouse.getJdbcUrl()}",
                        "app.clickhouse.username=${clickhouse.getUsername()}",
                        "app.clickhouse.password=${clickhouse.getPassword()}",
                        "spring.main.allow-bean-definition-overriding=true"
                    )
    }
}