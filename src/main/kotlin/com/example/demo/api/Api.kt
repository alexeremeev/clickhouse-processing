package com.example.demo.api

import com.example.demo.services.AggregateService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router

@Configuration
class Api(val aggregateHandler: AggregateHandler) {

    @Bean
    fun router() = router {
        GET("/users/{userId}").nest {
            GET("/quantity") { req -> aggregateHandler.find(req.userId()) }
            GET("/buckets/{bucket}/quantity") { req ->
                aggregateHandler.find(
                    req.userId(),
                    req.bucket()
                )
            }
        }
    }

    @Component
    class AggregateHandler(private val aggregateService: AggregateService) {

        fun find(userId: String) = toResponse(aggregateService.find(userId))
        fun find(userId: String, bucket: String) = toResponse(aggregateService.find(userId, bucket))

        private fun <T> toResponse(value: T?) =
            value?.run { ok().body(BodyInserters.fromValue(this)) } ?: notFound().build()
    }

    private fun ServerRequest.userId() = this.pathVariable("userId")

    private fun ServerRequest.bucket() = this.pathVariable("bucket")
}
