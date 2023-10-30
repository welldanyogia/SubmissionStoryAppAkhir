package com.example.submissionstoryapp.data.api

import com.example.submissionstoryapp.data.response.DetailResponse
import com.example.submissionstoryapp.data.response.FileUploadResponse
import com.example.submissionstoryapp.data.response.LoginResponse
import com.example.submissionstoryapp.data.response.RegisterResponse
import com.example.submissionstoryapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("location") location : Int = 0,
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesLoc(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("location") location : Int = 1,
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    fun addStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<FileUploadResponse>

    @Multipart
    @POST("stories")
    fun addStoryWithLoc(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): Call<FileUploadResponse>
}