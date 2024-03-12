package com.caching.controller

import CachingTest
import com.caching.model.Journey
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

class EventSourceTest @Autowired constructor() : CachingTest() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext
    private lateinit var mockMvc: MockMvc
    private val USER_ID_HEADER = "api-user-id"

    @Value("classpath:journey.json")
    private lateinit var journeyJsonFile: Resource
    @Value("classpath:journeys.json")
    private lateinit var journeysJsonFile: Resource

    @Autowired
    private var redisTemplate = RedisTemplate<String, String>()
    private final val mapper: ObjectMapper = ObjectMapper()
    private final val firstUserJourneyList = listOf(Journey("100"), Journey("200"))
    private final val secondUserJourneyList = listOf(Journey("100"), Journey("300"))
    private final val thirdUserJourneyList = listOf(Journey("500"))
    val firstUserJourneyListAsString: String = mapper.writeValueAsString(firstUserJourneyList)
    val secondUserJourneyListAsString: String = mapper.writeValueAsString(secondUserJourneyList)
    val thirdUserJourneyListAsString: String = mapper.writeValueAsString(thirdUserJourneyList)
    val firstUserId = 123
    val secondUserId = 456
    val thirdUserId = 789

    @BeforeEach
    fun saveJourneys() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build()
        redisTemplate.opsForValue().set(firstUserId.toString(), firstUserJourneyListAsString)
        redisTemplate.opsForValue().set(secondUserId.toString(), secondUserJourneyListAsString)
        redisTemplate.opsForValue().set(thirdUserId.toString(), thirdUserJourneyListAsString)
    }

    @AfterEach
    fun flush() {
        redisTemplate.connectionFactory?.connection?.flushAll()
    }

    // GET journey tests

    @Test
    fun `GET journey endpoint should return 200 and single journey by user when user ID and journey ID are valid`() {
        val journeyData = journeyJsonFile.inputStream.readBytes().toString(Charsets.UTF_8)
        val result = mockMvc.perform(
            get("/api/journey?journey_id=100")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val content = result.response.contentAsString

        JSONAssert.assertEquals(journeyData, content, true)
    }

    @Test
    fun `GET journey endpoint should return 200 when user ID is a valid string`() {
        mockMvc.perform(
            get("/api/journey?journey_id=500")
                .header(USER_ID_HEADER, thirdUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 404 when journey ID is not found`() {
        mockMvc.perform(
            get("/api/journey?journey_id=1")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 404 when user ID is not found`() {
        mockMvc.perform(
            get("/api/journey?journey_id=100")
                .header(USER_ID_HEADER, 404)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when journey ID is invalid`() {
        mockMvc.perform(
            get("/api/journey?journey_id=Invalid@Id")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when journey ID is empty`() {
        mockMvc.perform(
            get("/api/journey?journey_id=")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when journey ID is whitespace`() {
        mockMvc.perform(
            get("/api/journey?journey_id= ")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when user ID is invalid`() {
        mockMvc.perform(
            get("/api/journey?journey_id=100")
                .header(USER_ID_HEADER, -123)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when user ID is invalid (2)`() {
        mockMvc.perform(
            get("/api/journey?journey_id=100")
                .header(USER_ID_HEADER, -0.22)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journey endpoint should return 400 when user ID is an invalid string`() {
        mockMvc.perform(
            get("/api/journey?journey_id=100")
                .header(USER_ID_HEADER, "Invalid")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    // GET journeys tests

    @Test
    fun `GET journeys endpoint should return 200 and journey list by user when user ID is valid`() {
        val journeysData = journeysJsonFile.inputStream.readBytes().toString(Charsets.UTF_8)
        val result = mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, firstUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val content = result.response.contentAsString

        JSONAssert.assertEquals(journeysData, content, true)
    }

    @Test
    fun `GET journeys endpoint should return 200 when user ID is a valid string`() {
        mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, secondUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
    }

    @Test
    fun `GET journeys endpoint should return 404 when user ID is not found`() {
        mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, 404)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andReturn()
    }

    @Test
    fun `GET journeys endpoint should return 400 when user ID is invalid`() {
        mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, -100)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journeys endpoint should return 400 when user ID is invalid (2)`() {
        mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, 0.5)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `GET journeys endpoint should return 400 when user ID is an invalid string`() {
        mockMvc.perform(
            get("/api/user/journeys")
                .header(USER_ID_HEADER, "Invalid")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }
}
