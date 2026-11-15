package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface QuranApiService {
    @GET("surah/{number}/quran-uthmani")
    suspend fun getSurahUthmani(@Path("number") surahNumber: Int): SurahResponse

    @GET("quran/quran-uthmani")
    suspend fun getFullQuranUthmani(): FullQuranResponse

    companion object {
        private const val BASE_URL = "https://api.alquran.cloud/v1/"

        fun create(): QuranApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(QuranApiService::class.java)
        }
    }
}
