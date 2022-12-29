package com.kotlincoffeemaker.application.advice

class UnknownProductVolumeException(override val message: String?): RuntimeException(message)