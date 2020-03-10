package com.example.demo.services

import com.example.demo.utils.toEvent
import org.springframework.stereotype.Service

@Service
class EventProcessor(private val service: AggregateService) {

    fun process(messages: List<ByteArray>) {
        val events = messages.asSequence()
            .map { it.toEvent() }
            .toList()

        service.save(events)
    }

}