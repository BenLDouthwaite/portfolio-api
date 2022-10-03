package com.bendouthwaite.portfolioapi

import com.bendouthwaite.portfolioapi.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class PortfolioApiApplication

fun main(args: Array<String>) {
	runApplication<PortfolioApiApplication>(*args)
}


