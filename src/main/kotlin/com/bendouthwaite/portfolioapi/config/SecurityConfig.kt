package com.bendouthwaite.portfolioapi.config

import com.bendouthwaite.portfolioapi.security.CustomUserDetailsService
import com.bendouthwaite.portfolioapi.security.RestAuthenticationEntryPoint
import com.bendouthwaite.portfolioapi.security.TokenAuthenticationFilter
import com.bendouthwaite.portfolioapi.security.oauth2.CustomOAuth2UserService
import com.bendouthwaite.portfolioapi.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.bendouthwaite.portfolioapi.security.oauth2.OAuth2AuthenticationFailureHandler
import com.bendouthwaite.portfolioapi.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    @Autowired
    private var customUserDetailsService: CustomUserDetailsService,
    @Autowired
    private val customOAuth2UserService: CustomOAuth2UserService,
    @Autowired
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    @Autowired
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter()
    }

    /*
      By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
      the authorization request. But, since our service is stateless, we can't save it in
      the session. We'll save the request in a Base64 encoded cookie instead.
    */
    @Bean
    fun cookieAuthorizationRequestRepository(): HttpCookieOAuth2AuthorizationRequestRepository {
        return HttpCookieOAuth2AuthorizationRequestRepository()
    }

    @Throws(Exception::class)
    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
            .userDetailsService<UserDetailsService>(customUserDetailsService)
            .passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }


    // TODO Need to actually test auth being triggered from localhost, / the frontend app.
    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http
            // TODO Review CORS config
            // https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application
//            .cors().configurationSource { request ->
////                CorsConfiguration().applyPermitDefaultValues()
//                val cors = CorsConfiguration()
//                cors.allowedOrigins = listOf("http://localhost:3000", "http://localhost:8080","http://127.0.0.1:80", "http://example.com")
//                cors.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                cors.allowedHeaders = listOf("*")
//                cors
//            }
            .cors()
                .and()
            .csrf()
                .disable() // TODO Is this needed?

            // TESTING
            .formLogin()
                .disable()
            .httpBasic()
                .disable()
//            .exceptionHandling { e ->
//                e.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//            }
            .exceptionHandling()
                .authenticationEntryPoint(RestAuthenticationEntryPoint())
                .and()


            .authorizeRequests { a -> a
                    .antMatchers(
                        "/", "/error", "/webjars/**", "/any-user",
                    )
                        .permitAll()
                    .antMatchers("/auth/**", "/oauth2/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
            }
                // Gives '401' instead of distracting CORS errors

            .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                    .and()
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);


        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

    }

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.setAllowedOrigins(listOf("*"))
////        configuration.setAllowedOriginPatterns(listOf("*"))
//        configuration.addAllowedHeader("*")
//        configuration.addAllowedMethod("*")
//        configuration.allowCredentials = true
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }
}