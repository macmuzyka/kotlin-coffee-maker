package com.kotlincoffeemaker.application.validation

import com.kotlincoffeemaker.application.advice.InvalidDisplayMode
import com.kotlincoffeemaker.application.model.enums.CoffeeDosage
import com.kotlincoffeemaker.application.model.enums.DisplayMode
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Slf4j
class ParamValidator {
    companion object {
        private val log = LoggerFactory.getLogger(ParamValidator::class.java)

        fun validateDosage(dosage: List<CoffeeDosage>?): List<String> {
            return if (dosage == null) {
                val values: Array<CoffeeDosage> = CoffeeDosage.values()
                val newDosage: MutableList<String> = ArrayList()
                for (dos in values) {
                    newDosage.add(dos.toString())
                }
                newDosage
            } else {
                val newDosage: MutableList<String> = ArrayList()
                for (dos in dosage) {
                    newDosage.add(dos.toString())
                }
                newDosage
            }
        }

        fun validateName(clientName: String?, creating: Boolean): String {
            return if (creating) {
                clientName ?: "Have a nice nice coffee :)"
            } else {
                clientName ?: ""
            }

        }

        fun validateDisplayMode(mode: DisplayMode): String {
            return when (mode) {
                DisplayMode.TODAY -> {
                    log.info("TODAY case")
                    LocalDateTime.now().toString()
                }

                DisplayMode.ALL_TIME -> {
                    log.info("ALL_TIME case")
                    LocalDateTime.now().minusYears(25).toString()
                }

                else -> { // conditions exhausted, but going to keep it
                    throw InvalidDisplayMode("Unrecognized display mode selected!")
                }
            }
        }
    }
}


