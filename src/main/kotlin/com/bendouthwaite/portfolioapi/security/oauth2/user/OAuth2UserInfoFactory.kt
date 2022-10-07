package com.bendouthwaite.portfolioapi.security.oauth2.user

import com.bendouthwaite.portfolioapi.exception.OAuth2AuthenticationProcessingException
import com.bendouthwaite.portfolioapi.model.AuthProvider

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String?, Any?>?): OAuth2UserInfo {
        return if (registrationId.equals(
                AuthProvider.github.toString(),
                ignoreCase = true
            )
        ) {
            GithubOAuth2UserInfo(attributes)
        } else {
            throw OAuth2AuthenticationProcessingException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}