package com.example.demo.listener

import com.example.demo.configs.AbstractIntegrationTest
import com.example.demo.configs.TestConfig
import com.example.demo.configs.TestConfig.Companion.TEST_USER
import com.example.demo.services.AggregateService
import com.example.demo.services.EventProcessor
import com.example.demo.utils.readFolder
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = TestConfig.CustomContextLoader::class)
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"])
@TestConstructor(autowireMode = ALL)
internal class EventListenerTest(
    val processor: EventProcessor,
    val aggregateService: AggregateService
) : AbstractIntegrationTest() {


    @Test
    fun `should process messages`() {
        val messages = readFolder("events1")
        processor.process(messages)
        val result = aggregateService.find(TEST_USER)

        assertNotNull(result)
        assertThat(BigDecimal.valueOf(20), comparesEqualTo(result?.quantity))
    }

    @Test
    fun `should process messages from different buckets`() {
        val messages = readFolder("events2")
        processor.process(messages)
        val aggregate = aggregateService.find(TEST_USER)

        assertNotNull(aggregate)
        assertThat(BigDecimal.valueOf(10), comparesEqualTo(aggregate?.quantity))

        val february = aggregateService.find(TEST_USER, "2020-02-02")
        val march = aggregateService.find(TEST_USER, "2020-02-03")

        assertThat(BigDecimal.ZERO, comparesEqualTo(february?.quantity))
        assertThat(BigDecimal.valueOf(10), comparesEqualTo(march?.quantity))
    }
}