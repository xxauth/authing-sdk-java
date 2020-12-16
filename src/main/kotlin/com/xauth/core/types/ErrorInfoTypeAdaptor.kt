package com.xauth.core.types

import com.google.gson.*
import com.xauth.core.graphql.GraphQLResponse
import java.lang.reflect.Type


class ErrorInfoTypeAdaptor : JsonDeserializer<GraphQLResponse.ErrorInfo> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GraphQLResponse.ErrorInfo {
        val data = JsonParser.parseString(json!!.asString)
        val msg = data.asJsonObject.get("errors").asJsonArray.get(0).asJsonObject.get("message").asJsonObject
        val code = msg.get("code")
        val message = msg.get("message")
        return GraphQLResponse.ErrorInfo(code = code.asInt, message = message.asString)
    }
}