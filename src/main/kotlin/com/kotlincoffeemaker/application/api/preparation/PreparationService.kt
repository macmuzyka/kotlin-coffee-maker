package com.kotlincoffeemaker.application.api.preparation

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlincoffeemaker.application.advice.AlreadyCompletedException
import com.kotlincoffeemaker.application.api.coffee.CoffeeService
import com.kotlincoffeemaker.application.api.order.OrderRepository
import com.kotlincoffeemaker.application.api.order.OrderService
import com.kotlincoffeemaker.application.api.product.ProductLevelService
import com.kotlincoffeemaker.application.api.property.PropertyContainer
import com.kotlincoffeemaker.application.model.Coffee
import com.kotlincoffeemaker.application.model.ProductLevelContainer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PreparationService(
    val orderRepository: OrderRepository,
    val coffeeService: CoffeeService,
    val orderService: OrderService,
    val productLevelService: ProductLevelService
) {

    private val log = LoggerFactory.getLogger(PreparationService::class.java)

    fun brewCoffee(coffeeId: Long): Coffee {
        return try {
            var coffeeToBrew = coffeeService.getCoffeeById(coffeeId)
            val currentState = productLevelService.currentState()
            val enough = verifyProducts(currentState)

            if (enough) {
                try {
                    val coffeeBrewed = coffeeToBrew.brew()
                    if (coffeeBrewed) {
                        log.info("Adjusting product levels after brewing coffee..")
                        productLevelService.productUsage(coffeeToBrew)
                    }
                } catch (e: Exception) {
                    log.error("Something went wrong while brewing coffee with id $coffeeId")
                }

                val brewed = coffeeService.updateCoffee(coffeeToBrew)
                log.info("Brewed Coffee :: $brewed")
                verifyCoffeeOrder(coffeeId)
            }

            coffeeToBrew
        } catch (ace: AlreadyCompletedException) {
            throw ace
        }
    }

    private fun verifyCoffeeOrder(coffeeId: Long) {
        var orderToVerify = orderRepository.findOrderContainingCoffeeId(coffeeId).get()
        orderToVerify.let {
            val orderNotReady = it.coffeeList
                .stream()
                .anyMatch { coffee -> !coffee.coffeeComplete }
            if (orderNotReady) {
                log.info("Missing completed coffee within order ${orderToVerify.id}")
            } else {
                log.error("Probably all coffee is ready within order ${orderToVerify.id}, attempting to complete order..")
                orderService.completeOrder(orderToVerify)
                orderRepository.save(orderToVerify)
            }
        }
    }

    private fun verifyProducts(currentState: ProductLevelContainer): Boolean {
        val mapper = ObjectMapper()
        val products = mapper.convertValue(currentState, Map::class.java)
        var enough = true
        val minimumProductLevel = PropertyContainer.minimumProductLevel

        for (product in products) {
            try {
                val value = product.value.toString().toDouble()
                if (value < minimumProductLevel) {
                    log.error("Not enough of ${product.key} ingredient to start coffee brewing process")
                    enough = false
                    break
                }
            } catch (e: Exception) {
                // do nothing, just catch exception
            }
        }
        return enough
    }

    @Scheduled(cron = "\${orders.to.deliver.reminder}")
    fun toDeliver() {
        val toBeDelivered = orderRepository.findReadyToDeliver()
        if (toBeDelivered.isNotEmpty()) {
            for (ready in toBeDelivered) {
                log.info("Order to deliver :: $ready")
            }
        }
    }
}