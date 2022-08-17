package com.bendouthwaite.portfolioapi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {

    @GetMapping("/tasks")
    fun taskList(): List<String> {
        return listOf("task 1", "task 2")
    }
}