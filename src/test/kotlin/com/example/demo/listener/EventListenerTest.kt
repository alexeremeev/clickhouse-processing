package com.example.demo.listener

import com.example.demo.configs.AbstractIntegrationTest
import com.example.demo.configs.TestConfig
import com.example.demo.services.AggregateService
import com.example.demo.services.EventProcessor
import org.hamcrest.Matchers
import org.hamcrest.Matchers.comparesEqualTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.shaded.com.google.common.io.Resources.getResource
import org.testcontainers.shaded.com.google.common.io.Resources.toByteArray
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

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
        val result = aggregateService.find(TestConfig.firstUser)

        assertNotNull(result)
        assertThat(BigDecimal.valueOf(20), comparesEqualTo(result?.quantity))

    }

    private fun readFolder(folderName: String) =
        Files.walk(Paths.get(File("src/test/resources/json/$folderName").absolutePath)).use { stream ->
            stream.filter { Files.isRegularFile(it) }
                .map { toByteArray(getResource("json/$folderName/${it.fileName}")) }
                .toList()
        }
}