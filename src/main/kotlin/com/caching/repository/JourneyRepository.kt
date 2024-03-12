package com.caching.repository

import com.caching.model.Journey
import org.springframework.data.repository.CrudRepository

interface JourneyRepository : CrudRepository<Journey, String>
