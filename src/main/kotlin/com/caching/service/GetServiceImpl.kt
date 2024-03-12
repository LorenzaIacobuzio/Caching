package com.caching.service

import com.caching.model.Journey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class GetServiceImpl(
    @Autowired
    private val redisTemplate: RedisTemplate<String, String> = RedisTemplate<String, String>()
) : GetService {
    override fun getJourney(userId: String, journeyId: String): Journey? {
        val journeyList: List<Journey>? = getJourneys(userId)
        val journey: Journey? = journeyList?.firstOrNull() { j -> j.id == journeyId }

        return journey
    }

    override fun getJourneys(userId: String): List<Journey>? {
        val journeyListAsString = redisTemplate.opsForValue().get(userId)
        val mapper = ObjectMapper()
        val journeyList: List<Journey>? = try {
            mapper.readValue(
                journeyListAsString, mapper.typeFactory.constructCollectionType(
                    MutableList::class.java,
                    Journey::class.java
                )
            )
        } catch (e: Exception) {
            null
        }

        return journeyList
    }
}
