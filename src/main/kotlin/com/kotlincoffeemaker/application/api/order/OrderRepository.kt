package com.kotlincoffeemaker.application.api.order

import com.kotlincoffeemaker.application.model.CoffeeOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Repository
interface OrderRepository : JpaRepository<CoffeeOrder, Long> {

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee_order co " +
                "INNER JOIN coffee_order_coffee_list cl ON co.id = cl.coffee_order_id " +
                "WHERE cl.coffee_list_id = :coffeeId "
    )
    fun findOrderContainingCoffeeId(
        @RequestParam(value = "coffeeId") coffeeId: Long?
    ): Optional<CoffeeOrder>

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee_order " +
                "WHERE TO_CHAR(DATE(last_modified), 'YYYY-MM-DD') >= TO_CHAR(DATE(:displayMode), 'YYYY-MM-DD') "
    )
    fun findAll(
        @RequestParam("displayMode") displayMode: String,
        pageable: Pageable
    ): Page<CoffeeOrder>

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee_order " +
                "WHERE ready = true " +
                "AND delivered = false "
    )
    fun findReadyToDeliver(): List<CoffeeOrder>

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee_order co " +
                "WHERE TO_CHAR(DATE(last_modified), 'YYYY-MM-DD') >= TO_CHAR(DATE(:displayMode), 'YYYY-MM-DD') " +
                "AND ready = true AND delivered = false "
    )
    fun findReadyButUndelivered(
        @RequestParam("displayMode") displayMode: String,
        pageable: Pageable
    ): Page<CoffeeOrder>

    @Query(
        nativeQuery = true, value = "SELECT * FROM coffee_order " +
                "WHERE TO_CHAR(DATE(last_modified), 'YYYY-MM-DD') >= TO_CHAR(DATE(:displayMode), 'YYYY-MM-DD') " +
                "AND ready = true AND delivered = true "
    )
    fun findReadyAndDelivered(
        @RequestParam("displayMode") displayMode: String,
        pageable: Pageable
    ): Page<CoffeeOrder>
}
