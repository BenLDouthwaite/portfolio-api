package com.bendouthwaite.portfolioapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {


    // TODO Need to actually test auth being triggered from localhost, / the frontend app.
    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http
            // TODO Review CORS config
            // https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application
            .cors().configurationSource { request -> CorsConfiguration().applyPermitDefaultValues() }
            .and()
            .authorizeRequests { a ->
                a
                    .antMatchers("/", "/error", "/webjars/**", "/any-user").permitAll()
                    .anyRequest().authenticated()
            }

            .oauth2Login()
    }


}