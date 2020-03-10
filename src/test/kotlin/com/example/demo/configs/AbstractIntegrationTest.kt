package com.example.demo.configs

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Connection

@SpringBootTest
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var connection: Connection

    @AfterEach
    fun tearDown() {
        connection.prepareStatement("truncate table quantities").execute()
    }
}