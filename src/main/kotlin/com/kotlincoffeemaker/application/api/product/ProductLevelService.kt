package com.kotlincoffeemaker.application.api.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlincoffeemaker.application.advice.UnableToGetProductContainerException
import com.kotlincoffeemaker.application.advice.UnknownProductVolumeException
import com.kotlincoffeemaker.application.api.property.Product
import com.kotlincoffeemaker.application.model.Coffee
import com.kotlincoffeemaker.application.model.ProductLevelContainer
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.ExtraIngredient
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ProductLevelService(val productContainer: ProductLevelContainerRepository) {

    private val log = LoggerFactory.getLogger(ProductLevelService::class.java)

    @Scheduled(cron = "\${material.level.check}")
    fun checkLevel() {

        if (productContainer.findAll().size < 1) {
            log.info("Application warmup :: product level container has to be initialized!")
            val newContainer = ProductLevelContainer(
                100.0,
                100.0,
                100.0,
                100.0,
                100.0,
                100.0,
                100.0,
                100.0
            )
            productContainer.save(newContainer)
        }

        log.info("Checking product levels...")
        var fine = true

        val container = productContainer.findContainer()

        val mapper = ObjectMapper()
        val products = mapper.convertValue(container, MutableMap::class.java)
        val warningLevel = Product.warningLevel

        for (product in products) {
            try {
                val value = product.value.toString().toDouble()
                if (value <= warningLevel) {
                    log.info("${product.key} level below warning threshold, it is :: ${product.value}")
                    fine = false
                }
            } catch (e: Exception) {
                // do nothing, just catch exception
            }
        }

        if (!fine) {
            // TODO: implement refilling logic!
            log.info("We need to do something about it...")
        } else {
            // do nothing, continue
            log.info("All fine, we are good to go!")
        }
    }

    fun productUsage(brewedCoffee: Coffee) {
        val container = productContainer.findContainer()
        container?.let {
            log.info("container pre update :: $container")
            var toSubtract: Double
            val dosage = brewedCoffee.dosage

            toSubtract = when (dosage) {
                CoffeeDosage.SINGLE -> 0.5
                CoffeeDosage.DOUBLE -> 1.0
                CoffeeDosage.TRIPLE -> 1.5
                else -> throw UnknownProductVolumeException("Cannot determine how much of product should be subtracted!")
            }
            container.coffeeGrain -= toSubtract

            val ingredients = brewedCoffee.ingredients
            ingredients?.let {
                for (ingredient in ingredients) {
                    when (ingredient) {
                        ExtraIngredient.MILK -> container.milk -= 0.5
                        ExtraIngredient.SOY_MILK -> container.soyMilk -= 0.4
                        ExtraIngredient.SUGAR -> container.sugar -= 0.5
                        ExtraIngredient.CANE_SUGAR -> container.caneSugar -= 0.4
                        ExtraIngredient.BROWN_SUGAR -> container.brownSugar -= 0.45
                        ExtraIngredient.CLOVES -> container.cloves -= 0.25
                        ExtraIngredient.CINNAMON -> container.cinnamon -= 0.25
                    }

                }
                val changedContainer = productContainer.save(container)
                log.info("changedContainer :: $changedContainer")
            }
        }
    }

    fun currentState(): ProductLevelContainer {
        return productContainer.findContainer()
            ?: throw UnableToGetProductContainerException("Product container could not be found in database!")
    }
}