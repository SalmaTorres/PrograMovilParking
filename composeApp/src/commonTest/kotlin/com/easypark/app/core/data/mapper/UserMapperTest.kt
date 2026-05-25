package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.dto.UserDTO
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType
import kotlin.test.Test
import kotlin.test.assertEquals

class UserMapperTest {
    @Test
    fun userRoundTripPreservesLocalFields() {
        val source = UserModel(
            id = 42,
            name = "Melany",
            type = UserType.OWNER,
            email = "melany@example.com",
            password = "secret1",
            cellphone = 76543210
        )

        val result = source.toEntity().toModel()

        assertEquals(source, result)
    }

    @Test
    fun invalidRemoteRoleFallsBackToDriver() {
        val result = UserDTO(id = 7, name = "Ana", type = "UNKNOWN", email = "a@b.com").toDomain()

        assertEquals(7, result.id)
        assertEquals(UserType.DRIVER, result.type)
        assertEquals("", result.password)
    }
}
