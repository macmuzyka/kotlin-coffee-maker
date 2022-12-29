package com.kotlincoffeemaker.application

import com.kotlincoffeemaker.application.api.coffee.CoffeeRepository
import com.kotlincoffeemaker.application.api.order.OrderRepository
import com.kotlincoffeemaker.application.api.product.ProductLevelService
import com.kotlincoffeemaker.application.model.Coffee
import com.kotlincoffeemaker.application.model.CoffeeOrder
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import javax.transaction.Transactional


@Component
class KotlinCoffeeMakerApplicationWarmup(
    val orderRepository: OrderRepository,
    val coffeeRepository: CoffeeRepository,
    val productLevelService: ProductLevelService
) : ApplicationListener<ApplicationStartedEvent> {

    private val log = LoggerFactory.getLogger(KotlinCoffeeMakerApplicationWarmup::class.java)

    @Override
    @Transactional
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        log.info("ApplicationContext :: ${event.applicationContext}")

        productLevelService.checkLevel()

        if (orderRepository.findAll().size > 0) {
            log.info("Application warmup :: No need of populating order / coffee list!")
            log.info("Warmup order already created!")
        } else {
            val firstCoffee = coffeeRepository.save(
                Coffee(
                    CoffeeDosage.SINGLE,
                    listOf(ExtraIngredient.MILK, ExtraIngredient.SUGAR),
                    "First order of the day!",
                    warmup = true,
                    coffeeComplete = true
                )
            )
            log.info("Application warmup first Coffee :: $firstCoffee")

            val firstOrder =
                orderRepository.save(
                    CoffeeOrder(
                        mutableListOf(firstCoffee),
                        warmup = true,
                        ready = true,
                        delivered = true
                    )
                )
            log.info("Application warmup first Order :: $firstOrder")
            log.info("Warmup order completed!")
        }
        log.info("Warmup done!")
    }
}
