package com.kotlincoffeemaker.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class KotlinCoffeeMakerApplication

fun main(args: Array<String>) {
	runApplication<KotlinCoffeeMakerApplication>(*args)
}
