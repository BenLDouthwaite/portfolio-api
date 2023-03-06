package com.bendouthwaite.portfolioapi.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('USER')")
    fun taskList(): List<String> {
        return listOf("task 1", "task 2")
    }
}