package com.example.demo.configs

import com.example.demo.props.KafkaProps
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties.AckMode

@Configuration
@EnableConfigurationProperties(KafkaProps::class)
class KafkaConfig(val props: KafkaProps) {

    @Bean
    fun kafkaListenerContainerFactory() =
        ConcurrentKafkaListenerContainerFactory<String, ByteArray>().apply {
            consumerFactory = consumerFactory()
            isBatchListener = true
            containerProperties.ackMode = AckMode.BATCH
            setConcurrency(props.concurrencyLevel)
        }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, ByteArray> = DefaultKafkaConsumerFactory(
        mapOf(
            ConsumerConfig.GROUP_ID_CONFIG to props,
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to props.servers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java
        )
    )

    companion object Topics {
        const val events = "app.events"
    }

}