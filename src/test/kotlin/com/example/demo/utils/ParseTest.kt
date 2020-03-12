package com.example.demo.utils

import com.example.demo.models.Event
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ParseTest {

    @Test
    fun `should read event`() {
        val source = """{"id":"7a845c6b-cdda-4594-90b5-c1f169f3eb8e",
                        "userId":"test_user_1","bucket":"2020-02-29",
                        "quantity":2,
                        "timestamp":"2020-03-12T07:52:30.497657Z"}""".trimIndent()

        val event = mapper.readValue<Event>(source)

        assertEquals(UUID.fromString("7a845c6b-cdda-4594-90b5-c1f169f3eb8e"), event.id)
        assertEquals("test_user_1", event.userId)
        assertThat(BigDecimal.valueOf(2), comparesEqualTo(event.quantity))
        assertNotNull(event.timestamp)
    }
}