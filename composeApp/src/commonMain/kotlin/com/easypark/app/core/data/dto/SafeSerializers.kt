package com.easypark.app.core.data.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.booleanOrNull

@OptIn(ExperimentalSerializationApi::class)
object SafeIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeIntSerializer", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int? {
        val input = decoder as? JsonDecoder ?: return try { decoder.decodeInt() } catch (e: Exception) { null }
        return try {
            val element = input.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.intOrNull ?: element.content.toIntOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeInt(value)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
object SafeLongSerializer : KSerializer<Long?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeLongSerializer", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long? {
        val input = decoder as? JsonDecoder ?: return try { decoder.decodeLong() } catch (e: Exception) { null }
        return try {
            val element = input.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.longOrNull ?: element.content.toLongOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Long?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeLong(value)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
object SafeDoubleSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeDoubleSerializer", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double? {
        val input = decoder as? JsonDecoder ?: return try { decoder.decodeDouble() } catch (e: Exception) { null }
        return try {
            val element = input.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.doubleOrNull ?: element.content.toDoubleOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Double?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeDouble(value)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
object SafeBooleanSerializer : KSerializer<Boolean?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeBooleanSerializer", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean? {
        val input = decoder as? JsonDecoder ?: return try { decoder.decodeBoolean() } catch (e: Exception) { null }
        return try {
            val element = input.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.booleanOrNull ?: element.content.toBooleanStrictOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeBoolean(value)
        }
    }
}
