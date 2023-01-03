package com.kotlincoffeemaker.application.api.coffee

import com.kotlincoffeemaker.application.model.Coffee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.RequestParam


@Repository
interface CoffeeRepository : JpaRepository<Coffee, Long> {

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee " +
                "WHERE dosage IN :dosage " +
                "AND coffee_complete = :complete " +
                "AND client_name iLIKE %:clientName% " +
                "AND TO_CHAR(DATE(last_modified), 'YYYY-MM-DD') >= TO_CHAR(DATE(:displayMode), 'YYYY-MM-DD') "
    )
    fun findAll(
        @RequestParam("dosage") dosage: List<String>,
        @RequestParam("complete") complete: Boolean,
        @RequestParam("clientName") clientName: String,
        @RequestParam("displayMode") displayMode: String,
        pageable: Pageable
    ): Page<Coffee>

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM coffee " +
                "WHERE dosage IN :dosage " +
                "AND client_name iLIKE %:clientName% " +
                "AND TO_CHAR(DATE(last_modified), 'YYYY-MM-DD') >= TO_CHAR(DATE(:displayMode), 'YYYY-MM-DD') "
    )
    fun findAll(
        @RequestParam("dosage") dosage: List<String>,
        @RequestParam("clientName") clientName: String,
        @RequestParam("displayMode") displayMode: String,
        pageable: Pageable
    ): Page<Coffee>
}
