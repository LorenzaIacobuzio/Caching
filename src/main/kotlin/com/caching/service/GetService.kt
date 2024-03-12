package com.caching.service

import com.caching.model.Journey

interface GetService {
    fun getJourney(userId: String, journeyId: String): Journey?
    fun getJourneys(userId: String): List<Journey>?
}
