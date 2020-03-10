package com.example.demo.utils

import com.example.demo.models.Event
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test

class ParseTest {

    @Test
    fun `should read event`() {
        val source =
            """{"id":"7a845c6b-cdda-4594-90b5-c1f169f3eb8e","userId":"test_user_1","bucket":"2020-02-29","quantity":2}""".trimIndent()
        val event = mapper.readValue<Event>(source)

        event.toString()
    }

}