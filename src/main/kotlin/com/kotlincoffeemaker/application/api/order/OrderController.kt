package com.kotlincoffeemaker.application.api.order

import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.DisplayMode
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(val orderService: OrderService) {

    @GetMapping("/all")
    fun getAllOrders(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") rows: Int,
        @RequestParam(required = false) ready: Boolean?,
        @RequestParam(required = false) delivered: Boolean?,
        @RequestParam(defaultValue = "TODAY") mode: DisplayMode
    ): ResponseEntity<*> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrder(page, rows, ready, delivered, mode))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR :: ${e.message}")
        }
    }

    @PostMapping("/post")
    fun postOrder(
        @RequestParam(required = false) orderId: Long?,
        @RequestParam(required = false) coffeeId: Long?,
        @RequestParam(required = false) dosage: CoffeeDosage,
        @RequestParam(required = false) ingredients: List<ExtraIngredient>?,
        @RequestParam(defaultValue = "Have a nice coffee! :)") clientName: String
    ): ResponseEntity<*> {
        return try {
            ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrCreateOrder(orderId, coffeeId, dosage, ingredients, clientName))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR :: ${e.message}")
        }
    }
}