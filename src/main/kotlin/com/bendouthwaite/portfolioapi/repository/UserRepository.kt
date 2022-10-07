package com.bendouthwaite.portfolioapi.repository

import com.bendouthwaite.portfolioapi.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    // TODO Update to use nullables instead of optional
    fun findByUsername(username: String): Optional<User>

}