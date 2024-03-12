package com.caching

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Caching

fun main(args: Array<String>) {
    runApplication<Caching>(*args) {
    }
}