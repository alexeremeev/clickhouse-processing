package com.example.demo.repos

import com.example.demo.configs.AbstractIntegrationTest
import com.example.demo.configs.TestConfig.Companion.firstUser
import com.example.demo.configs.TestConfig.CustomContextLoader
import com.example.demo.models.Event
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.Instant.now
import java.util.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = CustomContextLoader::class)
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"])
@TestConstructor(autowireMode = ALL)
internal class RepositoryTest(
    val aggregateRepository: EventAggregateRepository,
    val eventRepository: EventRepository
) : AbstractIntegrationTest() {

    @Test
    fun `given single events should save`() {
        val quantity = BigDecimal.valueOf(2)
        val event = Event(
            UUID.randomUUID(),
            firstUser,
            "2020-02-29",
            BigDecimal.valueOf(2),
            now()
        )

        eventRepository.save(listOf(event))

        val result = aggregateRepository.findByUser(firstUser)

        assertNotNull(result)
        assertThat(quantity, comparesEqualTo(result?.quantity))
    }

    @Test
    fun `given multiple events should save and produce aggregate`() {
        val first = Event(
            UUID.randomUUID(),
            firstUser,
            "2020-02-29",
            BigDecimal.valueOf(3),
            now()
        )

        val second = Event(
            UUID.randomUUID(),
            firstUser,
            "2020-03-01",
            BigDecimal.valueOf(-4),
            now()
        )

        eventRepository.save(listOf(first, second))

        val result = aggregateRepository.findByUser(firstUser)

        assertNotNull(result)
        assertThat(BigDecimal.valueOf(-1), comparesEqualTo(result?.quantity))
    }

    @Test
    fun `given different buckets should produce separate aggregates`() {
        val february = "2020-02-29"
        val firstEvent = Event(
            UUID.randomUUID(),
            firstUser,
            february,
            BigDecimal.valueOf(3),
            now()
        )

        val march = "2020-03-01"
        val secondEvent = Event(
            UUID.randomUUID(),
            firstUser,
            march,
            BigDecimal.valueOf(-4),
            now()
        )

        eventRepository.save(listOf(firstEvent, secondEvent))

        val firstAggregate = aggregateRepository.findByUserAndBucket(firstUser, february)
        val secondAggregate = aggregateRepository.findByUserAndBucket(firstUser, march)

        assertNotNull(firstAggregate)
        assertNotNull(secondAggregate)

        assertEquals(february, firstAggregate?.bucket)
        assertEquals(march, secondAggregate?.bucket)
        assertThat(BigDecimal.valueOf(3), comparesEqualTo(firstAggregate?.quantity))
        assertThat(BigDecimal.valueOf(-4), comparesEqualTo(secondAggregate?.quantity))
    }

    @Test
    fun `given no data should return no value`() {
        val result = aggregateRepository.findByUser(firstUser)

        assertNull(result)
    }
}