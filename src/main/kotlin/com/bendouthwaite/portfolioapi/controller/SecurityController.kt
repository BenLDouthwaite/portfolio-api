package com.bendouthwaite.portfolioapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityController {

    @GetMapping("/any-user")
    fun open(): String {
        return "test endpoint. access granted for any user, no security"
    }
}