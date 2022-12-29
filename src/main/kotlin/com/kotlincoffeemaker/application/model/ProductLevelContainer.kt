package com.kotlincoffeemaker.application.model

import com.kotlincoffeemaker.application.model.audit.Audit
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners

@Entity
@EntityListeners(AuditingEntityListener::class)
data class ProductLevelContainer(
    var coffeeGrain: Double,
    var milk: Double,
    var soyMilk: Double,
    var sugar: Double,
    var brownSugar: Double,
    var caneSugar: Double,
    var cinnamon: Double,
    var cloves: Double
): Audit() {
    constructor() : this(100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0)
}
