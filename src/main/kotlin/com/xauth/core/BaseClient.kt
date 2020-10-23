package com.xauth.core

import com.xauth.core.graphql.GraphQLCall
import com.xauth.core.graphql.GraphQLRequest
import com.xauth.core.graphql.GraphQLResponse
import com.xauth.core.http.Call
import com.xauth.core.http.HttpCall
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 * Authing 客户端类
 */
abstract class BaseClient(private val userPoolId: String) {
    // 可选参数
    var host: String = "https://core.xauth.lucfish.com"
    var publicKey: String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb" +
            "5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae" +
            "+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uM" +
            "GKl64GDcIq3au+aqJQIDAQAB"

    /**
     * 每次发出请求时会附加在请求头的 AccessToken
     *
     * 访问某些需要权限的接口则必须设置此变量
     */
    var accessToken: String? = null

    // 常量
    private val mediaTypeJson: MediaType? = "application/json".toMediaTypeOrNull()
    private val sdkType: String = "SDK"
    private val sdkVersion: String = "java:2.0.5"

    // graphql 端点
    private val endpoint: String
        get() {
            return "$host/v2/graphql"
        }

    private val client: OkHttpClient = OkHttpClient()
    private val json = GsonBuilder().create()

    /**
     * 密码加密方法
     */
    protected open fun encrypt(msg: String): String {
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
    protected open fun <TResponse> createGraphQLCall(
        request: GraphQLRequest,
        typeToken: TypeToken<GraphQLResponse<TResponse>>
    ): Call<TResponse> {
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
            ), adapter
        )
    }

    /**
     * 创建 HTTP GET 请求
     */
    protected open fun <TResponse> createHttpGetCall(url: String, typeToken: TypeToken<TResponse>): Call<TResponse> {
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
    protected open fun <TResponse> createHttpPostCall(
        url: String,
        body: String,
        typeToken: TypeToken<TResponse>
    ): Call<TResponse> {
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
