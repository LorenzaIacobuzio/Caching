package com.caching.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableAutoConfiguration
class RedisConfig {

    @Autowired
    private lateinit var redisProperties: RedisProperties

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisConf = RedisStandaloneConfiguration()
        redisConf.hostName = redisProperties.host
        redisConf.port = redisProperties.port
        return LettuceConnectionFactory(redisConf)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(redisConnectionFactory())
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}