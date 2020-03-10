package com.example.demo.repos

import com.example.demo.models.Event
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.time.Instant.now

@Repository
class EventRepository(val connection: Connection) {

    fun save(events: Collection<Event>) {
        val query ="INSERT INTO quantities(user_id, bucket, quantity, updated_at) VALUES (?, ?, ?, ?)"

        connection.prepareStatement(query).run {
            events.forEach {
                var index = 0
                setString(++index, it.userId)
                setString(++index, it.bucket)
                setBigDecimal(++index, it.quantity)
                setLong(++index, now().epochSecond)
                addBatch()
            }

            executeBatch()
        }
    }
}