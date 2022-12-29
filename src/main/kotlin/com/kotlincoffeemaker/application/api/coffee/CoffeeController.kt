package com.kotlincoffeemaker.application.api.coffee

import com.kotlincoffeemaker.application.advice.AlreadyCompletedException
import com.kotlincoffeemaker.application.advice.InvalidDisplayMode
import com.kotlincoffeemaker.application.api.preparation.PreparationService
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.DisplayMode
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/coffee")
class CoffeeController(val coffeeService: CoffeeService, val preparationService: PreparationService) {

    @GetMapping("/all")
    fun getAllCoffee(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") rows: Int,
        @RequestParam(required = false) dosage: List<CoffeeDosage>?,
        @RequestParam(required = false) completed: Boolean?,
        @RequestParam(required = false) clientName: String?,
        @RequestParam(defaultValue = "TODAY") mode: DisplayMode
    ): ResponseEntity<*> {
        return try {
            ResponseEntity.status(HttpStatus.OK)
                .body(coffeeService.getAllCoffee(page, rows, dosage, completed, clientName, mode))
        } catch (idm: InvalidDisplayMode) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(idm.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR :: ${e.message}")
        }
    }

    @PostMapping("/order")
    fun orderCoffee(
        @RequestParam dosage: CoffeeDosage,
        @RequestParam ingredients: List<ExtraIngredient>?,
        @RequestParam clientName: String?
    ): ResponseEntity<*> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(coffeeService.orderCoffee(dosage, ingredients, clientName))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR :: ${e.message}")
        }
    }

    @PatchMapping("/brew-coffee")
    fun brewCoffee(@RequestParam coffeeId: Long): ResponseEntity<*> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(preparationService.brewCoffee(coffeeId))
        } catch (iea: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND :: ${iea.message}")
        } catch (ace: AlreadyCompletedException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST :: ${ace.message}")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR :: ${e.message}")
        }
    }
}