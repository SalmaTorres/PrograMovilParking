package com.easypark.app.core.data.mapper

import com.easypark.app.parkingdetails.data.dto.ReviewDTO
import com.easypark.app.parkingdetails.domain.model.ReviewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class ReviewMapperTest {
    @Test
    fun reviewRoundTripPreservesFields() {
        val source = ReviewModel(id = 9, userId = 3, parkingId = 12, rating = 4.5f)

        val result = source.toEntity().apply { id = source.id }.toModel()

        assertEquals(source, result)
    }

    @Test
    fun firebaseReviewMapsToDomainAndHandlesMissingValues() {
        val mapped = ReviewDTO(id = 2, userId = 4, parkingId = 6, rating = 5f).toDomain()
        val empty = ReviewDTO(id = null, userId = null, parkingId = null, rating = null).toDomain()

        assertEquals(ReviewModel(2, 4, 6, 5f), mapped)
        assertEquals(ReviewModel(0, 0, 0, 0f), empty)
    }
}
