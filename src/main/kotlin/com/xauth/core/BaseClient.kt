package com.xauth.core

import com.xauth.core.graphql.GraphQLCall
import com.xauth.core.graphql.GraphQLRequest
import com.xauth.core.graphql.GraphQLResponse
import com.xauth.core.http.HttpCall
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.xauth.core.types.ErrorInfoTypeAdaptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 * Authing 客户端类
 */
abstract class BaseClient(internal val userPoolId: String) {
    // 可选参数
    var host: String = "https://core.xauth.lucfish.com"
    var publicKey: String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXF2kRW8oaTPA7KZqqsAuDmmhh" +
            "fa1IbxjK3zincLjV5ICJBacxTrKM6T8w/7zTgO/dRin2fACO5d65eOE1R65L2Syt" +
            "FWjSMefU8E36cHaykoi0o79qSxlpN7UPnRR1n60kRqlcM0IZ9XOlFszK05aLOrVh" +
            "Hdspg836OaW98JYl0QIDAQAB"

    /**
     * 每次发出请求时会附加在请求头的 AccessToken
     *
     * 访问某些需要权限的接口则必须设置此变量
     */
    var accessToken: String? = null

    // 常量
    private val mediaTypeJson: MediaType? = "application/json".toMediaTypeOrNull()
    private val sdkType: String = "SDK"
    private val sdkVersion: String = "java:3.0.6"

    // graphql 端点
    private val endpoint: String
        get() {
            return "$host/v2/graphql"
        }

    private val client: OkHttpClient = OkHttpClient()
    private val json = GsonBuilder().registerTypeAdapter(GraphQLResponse.ErrorInfo::class.java, ErrorInfoTypeAdaptor()).create()

    /**
     * 密码加密方法
     */
    internal open fun encrypt(msg: String?): String {
        if (msg === null) {
            return ""
        }

        // get publicKey
        val keyBytes: ByteArray = Base64.getDecoder().decode(publicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val cipherMsg = cipher.doFinal(msg.toByteArray())

        return String(Base64.getEncoder().encode(cipherMsg))
    }

    /**
     * 创建 GraphQL 请求
     */
    internal open fun <TData, TResult> createGraphQLCall(
        request: GraphQLRequest,
        typeToken: TypeToken<GraphQLResponse<TData>>,
        resolver: (data: TData) -> TResult
    ): GraphQLCall<TData, TResult> {
        val adapter = json.getAdapter(typeToken)
        return GraphQLCall(
            client.newCall(
                Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", "Bearer " + this.accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-authing-userpool-id", userPoolId)
                    .addHeader("x-authing-request-from", sdkType)
                    .addHeader("x-authing-sdk-version", sdkVersion)
                    .post(json.toJson(request).toRequestBody(mediaTypeJson))
                    .build()
            ), adapter, resolver
        )
    }

    /**
     * 创建 HTTP GET 请求
     */
    internal open fun <TResponse> createHttpGetCall(url: String, typeToken: TypeToken<TResponse>): HttpCall<TResponse> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            client.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.accessToken)
                    .addHeader("x-authing-userpool-id", userPoolId)
                    .addHeader("x-authing-request-from", sdkType)
                    .addHeader("x-authing-sdk-version", sdkVersion)
                    .get()
                    .build()
            ), adapter
        )
    }


    /**
     * 创建 HTTP POST 请求
     */
    internal open fun <TResponse> createHttpPostCall(
        url: String,
        body: String,
        typeToken: TypeToken<TResponse>
    ): HttpCall<TResponse> {
        val adapter = json.getAdapter(typeToken)
        return HttpCall(
            client.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + this.accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-authing-userpool-id", userPoolId)
                    .addHeader("x-authing-request-from", sdkType)
                    .addHeader("x-authing-sdk-version", sdkVersion)
                    .post(body.toRequestBody(mediaTypeJson))
                    .build()
            ), adapter
        )
    }
}
