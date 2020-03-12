package com.example.demo.models

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Event(
    val id: UUID,
    val userId: String,
    val bucket: String,
    val quantity: BigDecimal,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", timezone = "UTC") val timestamp: Instant
)