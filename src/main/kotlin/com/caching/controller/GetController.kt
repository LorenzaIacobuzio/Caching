package com.caching.controller

import com.caching.model.Journey
import com.caching.schema.GetResponse
import com.caching.schema.GetResponseListWrapper
import com.caching.schema.GetResponseWrapper
import com.caching.service.GetService
import com.caching.exception.BadRequestException
import com.caching.exception.NotFoundException
import org.jetbrains.annotations.NotNull
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.PositiveOrZero
import kotlin.math.sign

@RestController
@RequestMapping("/api")
class GetController(private val getService: GetService, private val modelMapper: ModelMapper) {
    @GetMapping("/journey")
    fun getJourney(
        @NotNull
        @NotBlank
        @NotEmpty
        @Valid
        @PositiveOrZero
        @RequestHeader("api-user-id") userId: Long,
        @NotNull
        @NotBlank
        @NotEmpty
        @RequestParam(name = "journey_id", required = true) journeyId: String
    ): ResponseEntity<GetResponseWrapper> {
        if (!validateUserId(userId)) {
            throw BadRequestException("Invalid user ID")
        }

        if (!validateJourneyId(journeyId)) {
            throw BadRequestException("Invalid journey ID")
        }

        val journey: Journey = getService.getJourney(userId.toString(), journeyId) ?: throw NotFoundException("Journey")
        val getResponse: GetResponse = modelMapper.map(journey, GetResponse::class.java)
        val response = GetResponseWrapper(getResponse)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/journeys")
    fun getJourneys(
        @RequestHeader("api-user-id") userId: Long,
    ): ResponseEntity<GetResponseListWrapper> {
        if (!validateUserId(userId)) {
            throw BadRequestException("Invalid user ID")
        }

        val journeys: List<Journey> = getService.getJourneys(userId.toString()) ?: throw NotFoundException("User")
        val getResponse: List<GetResponse> = modelMapper.map(journeys, object : TypeToken<List<GetResponse?>?>() {}.type)
        val response = GetResponseListWrapper(getResponse)

        return ResponseEntity.ok(response)
    }
}

private fun validateUserId(userId: Long): Boolean {
    return userId.sign == 1 && userId.toString().isNotEmpty()
}

private fun validateJourneyId(journeyId: String): Boolean {
    return journeyId.toLongOrNull() != null
}
