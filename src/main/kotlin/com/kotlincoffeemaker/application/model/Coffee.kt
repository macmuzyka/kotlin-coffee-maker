package com.kotlincoffeemaker.application.model

import com.kotlincoffeemaker.application.model.audit.Audit
import com.kotlincoffeemaker.application.advice.AlreadyCompletedException
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Coffee(
    @Enumerated(value = EnumType.STRING)
    var dosage: CoffeeDosage?,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = ExtraIngredient::class)
    var ingredients: List<ExtraIngredient>?,
    var clientName: String?,
    private val warmup: Boolean = false,
    var coffeeComplete: Boolean = false
) : Audit() {

    constructor() : this(CoffeeDosage.SINGLE, listOf(ExtraIngredient.SUGAR), "Default constructor client name")

    fun brew(): Boolean {
        return if (coffeeComplete) {
            throw AlreadyCompletedException("Coffee with id $id has been already brewed!")
        } else {
            this.coffeeComplete = true
            true
        }
    }
}