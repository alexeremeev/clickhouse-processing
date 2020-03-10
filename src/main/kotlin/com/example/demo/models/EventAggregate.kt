package com.example.demo.models

import java.math.BigDecimal
import java.time.Instant

data class EventAggregate(
    val userId : String,
    val bucket: String,
    val quantity: BigDecimal,
    val updatedAt: Instant
)