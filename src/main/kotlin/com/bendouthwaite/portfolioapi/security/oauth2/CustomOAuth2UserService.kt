package com.bendouthwaite.portfolioapi.security.oauth2

import com.bendouthwaite.portfolioapi.model.User
import com.bendouthwaite.portfolioapi.repository.UserRepository
import com.bendouthwaite.portfolioapi.security.UserPrincipal
import com.bendouthwaite.portfolioapi.security.oauth2.user.GithubOAuth2UserInfo
import com.bendouthwaite.portfolioapi.security.oauth2.user.OAuth2UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    @Autowired
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        return try {
            processOAuth2User(oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = GithubOAuth2UserInfo(oAuth2User.attributes)

        // TODO - Configure to return the user principal directly, and enable config without DB
        val userOptional = userRepository.findByUsername(oAuth2UserInfo.username)
        val user: User = userOptional.orElse(registerNewUser(oAuth2UserInfo))
        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User(
            oAuth2UserInfo.username,
            oAuth2UserInfo.name
        )
        return userRepository.save(user)
    }

}