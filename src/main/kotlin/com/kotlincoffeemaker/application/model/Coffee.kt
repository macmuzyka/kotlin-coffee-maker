package com.kotlincoffeemaker.application.model

import com.kotlincoffeemaker.application.advice.CoffeeNotFoundException
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Id
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@NoArgsConstructor
data class Coffee(
    @Enumerated(value = EnumType.STRING)
    var dosage: CoffeeDosage,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY, targetClass = ExtraIngredient::class)
    var ingredient: List<ExtraIngredient>,
    var clientName: String
) {
    @Id
    @GeneratedValue
    val id: Long? = null
    private var coffeeComplete: Boolean = false
    @CreatedDate
    var created: LocalDateTime? = null
    @LastModifiedDate
    var lastModified: LocalDateTime? = null

    constructor() : this(CoffeeDosage.SINGLE, listOf(ExtraIngredient.SUGAR), "Default constructor client name")

    fun brew() {
        return if (coffeeComplete) {
            throw CoffeeNotFoundException("Coffee with id $id has been already brewed!")
        }
        else {
            coffeeComplete = true
        }
    }
}


