package com.bendouthwaite.portfolioapi.controller

import com.bendouthwaite.portfolioapi.exception.ResourceNotFoundException
import com.bendouthwaite.portfolioapi.model.User
import com.bendouthwaite.portfolioapi.repository.UserRepository
import com.bendouthwaite.portfolioapi.security.CurrentUser
import com.bendouthwaite.portfolioapi.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserController(
    @Autowired
    private val userRepository: UserRepository
) {

    @RequestMapping("/user")
    @CrossOrigin
    fun user(@AuthenticationPrincipal userPrincipal: UserPrincipal): Map<String?, Any?>? {
        return mapOf("name" to userPrincipal.name)
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): User? {
        val id = userPrincipal.id ?: throw ResourceNotFoundException("User", "id", userPrincipal.id)
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User", "id", userPrincipal.id) }
    }
}