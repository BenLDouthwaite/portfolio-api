package com.bendouthwaite.portfolioapi.security.oauth2

import com.bendouthwaite.portfolioapi.exception.OAuth2AuthenticationProcessingException
import com.bendouthwaite.portfolioapi.model.AuthProvider
import com.bendouthwaite.portfolioapi.model.User
import com.bendouthwaite.portfolioapi.repository.UserRepository
import com.bendouthwaite.portfolioapi.security.UserPrincipal
import com.bendouthwaite.portfolioapi.security.oauth2.user.OAuth2UserInfo
import com.bendouthwaite.portfolioapi.security.oauth2.user.OAuth2UserInfoFactory
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
            processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            oAuth2UserRequest.clientRegistration.registrationId,
            oAuth2User.attributes
        )
        //        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
//            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
//        }
        var email = oAuth2UserInfo.email
        if (email == null) {
            email = "test@test.com"
        }
        val userOptional = userRepository.findByEmail(email)
        var user: User
        if (userOptional.isPresent) {
            user = userOptional.get()
            if (user.provider != AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)) {
                throw OAuth2AuthenticationProcessingException(
                    "Looks like you're signed up with " +
                            user.provider + " account. Please use your " + user.provider +
                            " account to login."
                )
            }
            user = updateExistingUser(user, oAuth2UserInfo)
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }
        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User()
        user.provider =
            AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        user.providerId = oAuth2UserInfo.id
        user.name = oAuth2UserInfo.name
        var email = oAuth2UserInfo.email
        if (email == null) {
            email = "test@test.com"
        }
        user.email = email
        user.imageUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        existingUser.name = oAuth2UserInfo.name
        existingUser.imageUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(existingUser)
    }
}