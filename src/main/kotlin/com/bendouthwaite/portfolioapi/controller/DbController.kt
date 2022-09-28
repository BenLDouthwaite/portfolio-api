package com.bendouthwaite.portfolioapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource


@RestController
class DbController(
    private val dataSource: DataSource
) {

    @GetMapping("/db")
    fun db(model: MutableMap<String?, Any?>): List<String> {
        try {
            dataSource.connection.use { connection ->
                val stmt = connection.createStatement()
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
                stmt.executeUpdate("INSERT INTO ticks VALUES (now())")
                val rs = stmt.executeQuery("SELECT tick FROM ticks")
                val output = ArrayList<String>()
                while (rs.next()) {
                    output.add("Read from DB: " + rs.getTimestamp("tick"))
                }
                model["records"] = output
                return output
            }
        } catch (e: Exception) {
            model["message"] = e.message
            return listOf("error")
        }
    }
}