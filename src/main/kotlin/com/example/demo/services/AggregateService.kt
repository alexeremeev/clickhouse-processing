package com.example.demo.services

import com.example.demo.models.Event
import com.example.demo.repos.EventAggregateRepository
import com.example.demo.repos.EventRepository
import org.springframework.stereotype.Service

@Service
class AggregateService(
    val eventRepository: EventRepository,
    val aggregateRepository: EventAggregateRepository
) {

    fun find(userId: String) = aggregateRepository.findByUser(userId)

    fun find(userId: String, bucket: String) = aggregateRepository.findByUserAndBucket(userId, bucket)

    fun save(events: Collection<Event>) = eventRepository.save(events)
}