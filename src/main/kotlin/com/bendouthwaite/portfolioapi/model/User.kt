package com.bendouthwaite.portfolioapi.model

import javax.persistence.*

// https://github.com/spring-guides/tut-spring-boot-kotlin#persistence-with-jpa
// > Here we don’t use data classes with val properties because JPA is not designed to
// > work with immutable classes or the methods generated automatically by data classes.
@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)