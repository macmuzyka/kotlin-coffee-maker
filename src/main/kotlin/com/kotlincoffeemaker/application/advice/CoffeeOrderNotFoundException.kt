package com.kotlincoffeemaker.application.advice

class CoffeeOrderNotFoundException(override val message: String?): RuntimeException(message) {
}