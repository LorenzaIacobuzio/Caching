package com.caching.exception

class NotFoundException(private val resource: String) : RuntimeException() {

    fun getResource(): String {
        return resource
    }
}
