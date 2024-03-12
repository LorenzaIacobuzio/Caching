package com.caching.model

import org.springframework.data.redis.core.RedisHash

@RedisHash("Journey")
class Journey(
    val id: String? = null,
)
