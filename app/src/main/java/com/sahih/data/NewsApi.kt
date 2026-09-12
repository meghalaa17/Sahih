package com.sahih.data

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("rss/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("hl") lang: String = "en-MY",
        @Query("gl") country: String = "MY",
        @Query("ceid") ceid: String = "MY:en"
    ): RssFeed

    companion object {
        fun create(): NewsApi = Retrofit.Builder()
            .baseUrl("https://news.google.com/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }
}