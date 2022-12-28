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

    private val logger = LoggerFactory.getLogger(CoffeeService::class.java)

    fun getAllCoffee(
        page: Int,
        rows: Int,
        dosage: List<CoffeeDosage>?,
        completed: Boolean?,
        clientName: String?,
        mode: DisplayMode
    ): Page<Coffee> {
        val validDosage: List<String> = ParamValidator.validateDosage(dosage)
        val properClientName = ParamValidator.validateClientName(clientName)
        val displayMode: String = ParamValidator.validateDisplayMode(mode)
        val pageable: Pageable = PageRequest.of(page, rows)
        logger.info(
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

    fun createCoffee(dosage: CoffeeDosage, ingredients: List<ExtraIngredient>, clientName: String): Coffee {
        return coffeeRepository.save(Coffee(dosage, ingredients, clientName))
    }

    fun brewCoffee(coffeeId: Long): Coffee {
        val coffeeToBrew = getCoffeeById(coffeeId)
        coffeeToBrew.brew()
        return coffeeRepository.save(coffeeToBrew)
    }
}