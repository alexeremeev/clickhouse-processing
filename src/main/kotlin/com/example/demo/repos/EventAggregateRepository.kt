package com.example.demo.repos

import com.example.demo.models.EventAggregate
import org.springframework.stereotype.Repository
import ru.yandex.clickhouse.response.ClickHouseResultSet
import java.sql.Connection
import java.sql.PreparedStatement
import java.time.Instant

@Repository
class EventAggregateRepository(val connection: Connection) {

    fun findByUser(userId: String): EventAggregate? {

        val query = """SELECT user_id, sum(quantity) AS quantity, max(updated_at) AS updated_at FROM quantities
                    WHERE user_id = ? GROUP BY user_id""".trimIndent()

        val ps = connection.prepareStatement(query)
            .apply { setString(1, userId) }

        return ps.toSingle {
            EventAggregate(
                it.getString("user_id"),
                "",
                it.getBigDecimal("quantity"),
                Instant.ofEpochMilli(it.getTimestampAsLong("updated_at"))
            )
        }
    }

    fun findByUserAndBucket(userId: String, bucket: String) : EventAggregate? {
        val query = """SELECT user_id, sum(quantity) AS quantity, max(updated_at) AS updated_at FROM quantities
                    WHERE user_id = ? AND bucket = ? GROUP BY user_id, bucket""".trimIndent()

        val ps = connection.prepareStatement(query)
            .apply {
                setString(1, userId)
                setString(2, bucket)
            }

        return ps.toSingle {
            EventAggregate(
                it.getString("user_id"),
                bucket,
                it.getBigDecimal("quantity"),
                Instant.ofEpochMilli(it.getTimestampAsLong("updated_at"))
            )
        }
    }

    fun PreparedStatement.toList(convert: (ClickHouseResultSet) -> EventAggregate) =
        (this.executeQuery() as ClickHouseResultSet).use {
            generateSequence { if (it.next()) convert.invoke(it) else null }.toList()
        }

    fun PreparedStatement.toSingle(convert: (ClickHouseResultSet) -> EventAggregate) =
        (this.executeQuery() as ClickHouseResultSet).use {
            if (it.next()) convert.invoke(it) else null
        }
}