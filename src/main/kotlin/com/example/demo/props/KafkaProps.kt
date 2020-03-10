package com.example.demo.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("app.kafka")
data class KafkaProps(
    val servers: String,
    val groupId: String,
    val concurrencyLevel: Int
)
