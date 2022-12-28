package com.kotlincoffeemaker.application.advice

class CoffeeNotFoundException(override val message: String?): RuntimeException(message)