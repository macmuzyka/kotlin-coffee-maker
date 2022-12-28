package com.kotlincoffeemaker.application.model

import Audit
import com.kotlincoffeemaker.application.advice.AlreadyCompletedException
import com.kotlincoffeemaker.application.advice.ExcludingConditionsException
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class CoffeeOrder(
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Coffee::class)
    @OrderColumn(name = "coffee_id")
    var coffeeList: MutableList<Coffee>,
    var ready: Boolean = false,
    var delivered: Boolean = false,
    val warmup: Boolean = false
) : Audit() {
    constructor () : this(
        mutableListOf(
            Coffee(
                CoffeeDosage.DOUBLE,
                listOf(ExtraIngredient.MILK, ExtraIngredient.MILK),
                "Warmup Client Name"
            )
        ), true, true
    )

    fun completeOrder() {
        if (ready) {
            throw AlreadyCompletedException("Cannot complete Order that has been already completed!")
        }
        this.ready = true
    }

    fun newCoffeeInCompleteOrder() {
        if (!ready) {
            throw ExcludingConditionsException("Attempting to set order not ready as it is already done!")
        }
        this.ready = false
    }

    fun deliverOrder(): Boolean {
        if (delivered) {
            throw AlreadyCompletedException("Cannot deliver Order that has been already delivered!")
        }
        this.delivered = true
        return delivered
    }
}
