package com.bendouthwaite.portfolioapi.security.oauth2.user

class GithubOAuth2UserInfo(attributes: Map<String, Any?>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = (attributes["id"] as Int?).toString()
    override val username: String
        get() = attributes["login"] as String
    override val name: String
        get() = attributes["name"] as String

}