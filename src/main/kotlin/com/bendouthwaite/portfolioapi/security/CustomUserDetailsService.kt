package com.bendouthwaite.portfolioapi.security

import com.bendouthwaite.portfolioapi.exception.ResourceNotFoundException
import com.bendouthwaite.portfolioapi.model.User
import com.bendouthwaite.portfolioapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    @Autowired
    var userRepository: UserRepository
) : UserDetailsService {

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found with username : $username") }
        return UserPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: Long): UserDetails {
        val user: User = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User", "id", id) }
        val userPrincipal = UserPrincipal.create(user)
        return userPrincipal
    }
}