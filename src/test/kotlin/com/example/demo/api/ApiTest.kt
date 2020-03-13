package com.example.demo.api

import com.example.demo.configs.AbstractIntegrationTest
import com.example.demo.configs.TestConfig.Companion.TEST_USER
import com.example.demo.configs.TestConfig.CustomContextLoader
import com.example.demo.models.Event
import com.example.demo.services.AggregateService
import com.example.demo.utils.mapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.shaded.com.google.common.io.Resources

@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension::class)
@ContextConfiguration(loader = CustomContextLoader::class)
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"])
@TestConstructor(autowireMode = ALL)
class ApiTest(
    val client: WebTestClient,
    val service: AggregateService
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setUp() {
        val rawEvents = Resources.toByteArray(Resources.getResource("json/events.json"))
        val events = mapper.readValue<List<Event>>(rawEvents)
        service.save(events)
    }

    @Test
    fun `should get event by user`() {
        client.get()
            .uri("/users/${TEST_USER}/quantity")
            .exchange().expectStatus().isOk
            .expectBody()
            .jsonPath("userId").isEqualTo(TEST_USER)
            .jsonPath("bucket").isEqualTo("")
            .jsonPath("quantity").isEqualTo(21.000)
            .jsonPath("updatedAt").exists()
    }

    @Test
    fun `should get event by user and bucket`() {
        val february = "2020-02-02"

        val march = "2020-03-02"

        client.get()
            .uri("/users/${TEST_USER}/buckets/${february}/quantity")
            .exchange().expectStatus().isOk
            .expectBody()
            .jsonPath("userId").isEqualTo(TEST_USER)
            .jsonPath("bucket").isEqualTo(february)
            .jsonPath("quantity").isEqualTo(1.000)
            .jsonPath("updatedAt").exists()

        client.get()
            .uri("/users/${TEST_USER}/buckets/${march}/quantity")
            .exchange().expectStatus().isOk
            .expectBody()
            .jsonPath("userId").isEqualTo(TEST_USER)
            .jsonPath("bucket").isEqualTo(march)
            .jsonPath("quantity").isEqualTo(20.000)
            .jsonPath("updatedAt").exists()
    }

    @Test
    fun `should return not found on no data`() {
        client.get()
            .uri("/users/random_user_id/quantity")
            .exchange().expectStatus().isNotFound

        client.get()
            .uri("/users/${TEST_USER}/buckets/1993-08-01/quantity")
            .exchange().expectStatus().isNotFound
    }
}