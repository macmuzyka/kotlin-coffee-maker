package com.kotlincoffeemaker.application.api.product

import com.kotlincoffeemaker.application.model.ProductLevelContainer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductLevelContainerRepository : JpaRepository<ProductLevelContainer, Long> {
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM product_level_container " +
                "LIMIT 1 "
    )
    fun findContainer(): ProductLevelContainer?
}
