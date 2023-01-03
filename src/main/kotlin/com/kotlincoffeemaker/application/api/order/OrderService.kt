package com.kotlincoffeemaker.application.api.order

import com.kotlincoffeemaker.application.advice.AlreadyCompletedException
import com.kotlincoffeemaker.application.advice.CoffeeOrderNotFoundException
import com.kotlincoffeemaker.application.advice.ExcludingConditionsException
import com.kotlincoffeemaker.application.api.coffee.CoffeeService
import com.kotlincoffeemaker.application.model.Coffee
import com.kotlincoffeemaker.application.model.CoffeeOrder
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.DisplayMode
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import com.kotlincoffeemaker.application.validation.ParamValidator
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class OrderService(val orderRepository: OrderRepository, val coffeeService: CoffeeService) {

    private val log = LoggerFactory.getLogger(OrderService::class.java)

    fun getAllOrder(
        page: Int,
        rows: Int,
        ready: Boolean?,
        delivered: Boolean?,
        mode: DisplayMode
    ): Page<CoffeeOrder> {
        val pageable = PageRequest.of(page, rows)
        val validatedMode = ParamValidator.displayMode(mode)

        log.info(
            "Validated params -> " +
                    "page :: $page, " +
                    "rows :: $rows, " +
                    "delivered :: $delivered, " +
                    "ready :: $ready, " +
                    "displayMode :: $mode"
        )

        return when {
            ready == false && delivered == true -> throw ExcludingConditionsException("Order cannot be delivered and NOT ready at the same time!")
            ready == null && delivered == null -> orderRepository.findAll(validatedMode, pageable)
            ready == true && (delivered == null || delivered == false) -> orderRepository.findReadyButUndelivered(
                validatedMode,
                pageable
            )

            (ready == true || ready == null) && delivered == true -> orderRepository.findReadyAndDelivered(
                validatedMode,
                pageable
            )

            else -> orderRepository.findAll(pageable)
        }
    }

    fun updateOrCreateOrder(
        orderId: Long?,
        coffeeId: Long?,
        dosage: CoffeeDosage?,
        ingredients: List<ExtraIngredient>?,
        clientName: String?
    ): CoffeeOrder {
        log.info("coffeeId :: $coffeeId, orderId :: $orderId, dosage :: $dosage, ingredients :: $ingredients, clientName :: $clientName")

        if (orderId == null) {
            return if (coffeeId == null) {
                log.info("Order id was not given, make new order and coffee in it!")
                val coffee = coffeeService.orderCoffee(dosage, ingredients, clientName)
                orderRepository.save(CoffeeOrder(mutableListOf(coffee)))
            } else {
                log.info("Update existing coffee without order id passed!")
                orderRepository.save(updateOrder(coffeeId, dosage, ingredients, clientName))
            }
        } else {
            if (coffeeId == null) {
                log.info("Create new coffee within given order!")
                val newCoffee = coffeeService.orderCoffee(dosage, ingredients, clientName)
                var existingOrder = getExistingOrder(orderId)
                log.info("existingOrder just extracted :: {}", existingOrder)

                if (existingOrder.delivered) {
                    log.info("Cannot add new coffee to already delivered order!")
                    throw AlreadyCompletedException("updateOrCreateOrder :: Cannot add new coffee to already delivered order!")
                } else {
                    if (existingOrder.ready) {
                        log.info("Changing order status from READY to UNREADY as new coffee was ordered in this yet UNDELIVERED order!")
                        existingOrder.newCoffeeInCompleteOrder()
                    }

                    existingOrder.coffeeList.add(newCoffee)
                    log.info("existingOrder pre save :: {}", existingOrder)
                    return orderRepository.save(existingOrder)
                }
            } else {
                log.info("Update existing coffee within given order!")
                return orderRepository.save(updateOrder(coffeeId, dosage, ingredients, clientName))
            }
        }
    }

    private fun getExistingOrder(
        orderId: Long
    ): CoffeeOrder {
        var existingOrder = orderRepository.findById(orderId)
        if (existingOrder.isPresent) {
            return existingOrder.get()
        } else {
            throw CoffeeOrderNotFoundException("getExistingCoffeeOrder :: Order with id $orderId not found!")
        }
    }

    private fun updateOrder(
        coffeeId: Long,
        dosage: CoffeeDosage?,
        ingredients: List<ExtraIngredient>?,
        clientName: String?
    ): CoffeeOrder {
        val order = orderRepository.findOrderContainingCoffeeId(coffeeId).get()

        order.let {
            if (it.delivered) {
                throw AlreadyCompletedException("updateOrder :: Cannot update coffee in already delivered order!")
            }
            val coffeeList = it.coffeeList
            updateCoffeeInOrder(coffeeList, coffeeId, dosage, ingredients, clientName)
            return it
        }
    }

    private fun updateCoffeeInOrder(
        coffeeList: MutableList<Coffee>,
        toUpdateId: Long,
        dosage: CoffeeDosage?,
        ingredients: List<ExtraIngredient>?,
        clientName: String?
    ) {
        for (coffee in coffeeList) {
            if (coffee.id == toUpdateId) {
                log.info("Matching coffee found!")

                when {
                    coffee.coffeeComplete -> throw AlreadyCompletedException("updateCoffeeInOrder :: Cannot update coffee that has been already brewed!")
                    else -> coffeeService.updateCoffee(coffee, dosage, ingredients, clientName)
                }
            }
        }
    }

    fun completeOrder(order: CoffeeOrder) {
        order.completeOrder()
        log.info("Order ${order.id} completed!")
    }

    /*fun verifyOrders() {
        val coffeeOrders = orderRepository.findAll()
            .stream()
            .filter { order -> !order.ready }
            .collect(Collectors.toList())

        for (order in coffeeOrders) {
            val completed = order.coffeeList
                .stream()
                .map { it.coffeeComplete }
                .noneMatch { false }

            if (completed) {
                order.completeOrder()
                orderRepository.save(order)
                log.info("Scheduled :: verifyOrders -> Order with id :: ${order.id} is READY to be delivered!")
            }
        }
    }*/
}
