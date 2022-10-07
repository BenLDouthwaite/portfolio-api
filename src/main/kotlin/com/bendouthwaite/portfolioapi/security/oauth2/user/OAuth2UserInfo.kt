package com.bendouthwaite.portfolioapi.security.oauth2.user

abstract class OAuth2UserInfo(var attributes: Map<String, Any?>) {
    abstract val id: String
    abstract val username: String
    abstract val name: String
}