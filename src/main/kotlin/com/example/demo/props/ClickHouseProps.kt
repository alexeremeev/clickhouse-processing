package com.example.demo.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.clickhouse")
data class ClickHouseProps(
    val url: String,
    val username: String,
    val password: String
)