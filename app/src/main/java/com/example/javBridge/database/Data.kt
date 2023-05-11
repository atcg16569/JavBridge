package com.example.javBridge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Entity
@Serializable
data class Movie(
    @PrimaryKey val id: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate? = null,
    var actress: MutableSet<String>? = null,
    var studio: String? = null
)

@Entity
@Serializable
data class Url(
    @PrimaryKey val name: String,
    var link: String
)

object LocalDateSerializer:KSerializer<LocalDate>{
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDate",PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }

}
