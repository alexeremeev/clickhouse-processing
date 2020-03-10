package com.example.demo.models

import java.math.BigDecimal
import java.util.*

data class Event(
    val id: UUID,
    val userId: String,
    val bucket: String,
    val quantity: BigDecimal
)