package com.kotlincoffeemaker.application.model

import Audit
import javax.persistence.Entity

@Entity
data class ProductLevelContainer(
    val coffeeGrain: Double,
    val milk: Double,
    val soyMilk: Double,
    val sugar: Double,
    val brownSugar: Double,
    val caneSugar: Double,
    val cinnamon: Double,
    val cloves: Double
): Audit() {
    constructor() : this(100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0)
}
