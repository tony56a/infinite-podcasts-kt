package com.zharguy.infinitepodcast.common.mappers

import com.google.protobuf.StringValue
import com.google.protobuf.Timestamp
import com.google.protobuf.stringValue
import com.google.protobuf.timestamp
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class UtilMappers {

    fun mapTimestamp(value: Timestamp): OffsetDateTime {
        return OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(value.seconds, value.nanos.toLong()), ZoneOffset.UTC
        )
    }

    fun mapOffsetDateTimeToProto(value: OffsetDateTime): Timestamp {
        return timestamp {
            seconds = value.toEpochSecond()
            nanos = value.nano
        }
    }

    fun mapIdStringToUuid(protoValue: StringValue): UUID? {
        return protoValue.value?.let {
            UUID.fromString(it)
        }
    }

    fun mapUuidToStringValue(uuid: UUID?): StringValue {
        return uuid?.let {
            stringValue { value = uuid.toString() }
        } ?: StringValue.getDefaultInstance()
    }
}