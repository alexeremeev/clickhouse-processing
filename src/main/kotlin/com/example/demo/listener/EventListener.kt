package com.example.demo.listener

import com.example.demo.configs.KafkaConfig
import com.example.demo.services.EventProcessor
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EventListener(val processor: EventProcessor) {

    @KafkaListener(topics = [KafkaConfig.events])
    fun handle(messages: List<ByteArray>) = processor.process(messages)
}