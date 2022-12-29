package com.kotlincoffeemaker.application.api.coffee

import com.kotlincoffeemaker.application.model.Coffee
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.DisplayMode
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import com.kotlincoffeemaker.application.validation.ParamValidator
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.IllegalArgumentException

@Slf4j
@Service
class CoffeeService(val coffeeRepository: CoffeeRepository) {

    private val log = LoggerFactory.getLogger(CoffeeService::class.java)

    fun getAllCoffee(
        page: Int,
        rows: Int,
        dosage: List<CoffeeDosage>?,
        completed: Boolean?,
        clientName: String?,
        mode: DisplayMode
    ): Page<Coffee> {
        val validDosage: List<String> = ParamValidator.validateDosage(dosage)
        val properClientName = ParamValidator.validateName(clientName, creating = false)
        val displayMode: String = ParamValidator.validateDisplayMode(mode)
        val pageable: Pageable = PageRequest.of(page, rows)
        log.info(
            "Validated params -> " +
                    "page :: $page, " +
                    "rows :: $rows, " +
                    "validDosage :: $validDosage, " +
                    "completed :: $completed, " +
                    "properClientName :: $properClientName, " +
                    "displayMode :: $displayMode"
        )

        return if (completed == null) {
            coffeeRepository.findAll(validDosage, properClientName, displayMode, pageable)
        } else {
            coffeeRepository.findAll(validDosage, completed, properClientName, displayMode, pageable)
        }
    }

    fun getCoffeeById(coffeeId: Long): Coffee {
        return coffeeRepository.findById(coffeeId)
            .orElseThrow { IllegalArgumentException("Coffee with id $coffeeId not found!") }
    }

    fun orderCoffee(dosage: CoffeeDosage?, ingredients: List<ExtraIngredient>?, clientName: String?): Coffee {
        val properClientName = ParamValidator.validateName(clientName, creating = true)
        return coffeeRepository.save(Coffee(dosage, ingredients, properClientName))
    }

    fun updateCoffee(
        toUpdate: Coffee,
        dosage: CoffeeDosage?,
        ingredients: List<ExtraIngredient>?,
        clientName: String?
    ) {
        log.info("Coffee to update :: $toUpdate")
        if (dosage != null) {
            if (dosage != toUpdate.dosage) {
                log.info("Changing dosage from :: ${toUpdate.dosage} to $dosage")
                toUpdate.dosage = dosage
            } else {
                log.info("Dosage stays the same!")
            }
        }

        if (ingredients != null) {
            val ingredientsToUpdate = toUpdate.ingredients
            log.info("Changing ingredients from :: {}", ingredientsToUpdate)
            log.info("to ingredients :: {}", ingredients)
            toUpdate.ingredients = ingredients
        }

        if (clientName != "Have a nice nice coffee :)") {
            if (clientName == toUpdate.clientName) {
                log.info("Changing label from ${toUpdate.clientName} to $clientName")
                toUpdate.clientName = clientName
            }
        } else {
            log.info("Label stays the same")
        }
        log.info("Coffee updated :: $toUpdate")
    }

    fun updateCoffee(toUpdate: Coffee): Coffee {
        return coffeeRepository.save(toUpdate)
    }
}