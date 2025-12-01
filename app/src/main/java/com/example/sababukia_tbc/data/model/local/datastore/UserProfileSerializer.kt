package com.example.sababukia_tbc.data.model.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.sababukia_tbc.UserProfiles
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserProfileSerializer : Serializer<UserProfiles> {
    override val defaultValue: UserProfiles = UserProfiles.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProfiles {
        try {
            return UserProfiles.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserProfiles, output: OutputStream) {
        t.writeTo(output)
    }
}
