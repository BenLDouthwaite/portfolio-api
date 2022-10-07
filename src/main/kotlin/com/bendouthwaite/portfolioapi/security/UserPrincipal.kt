package com.bendouthwaite.portfolioapi.security

import com.bendouthwaite.portfolioapi.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class UserPrincipal(
    val id: Long?,

    private val name: String = "default_name",

    val email: String = "test@test.com",
    private val password: String = "password",
    private val authorities: Collection<GrantedAuthority> = listOf()
) : OAuth2User, UserDetails {
    private var attributes: Map<String, Any>? = mapOf()
    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getName(): String {
        return name
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes!!
    }

    fun setAttributes(attributes: Map<String, Any>?) {
        this.attributes = attributes
    }

//    override fun getName(): String {
//        return id.toString()
//    }

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = listOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))

            val userName = user.name

            return UserPrincipal(
                user.id,
                user.name ?: "Def_name",
                user.email ?: "test@test.com",
                user.password?: "password",
                authorities
            )
        }

        fun create(user: User, attributes: Map<String, Any>?): UserPrincipal {
            val userPrincipal = create(user)
            userPrincipal.setAttributes(attributes)
            return userPrincipal
        }
    }
}