package com.kotlincoffeemaker.application.advice

class AlreadyCompletedException(override val message: String): RuntimeException(message)