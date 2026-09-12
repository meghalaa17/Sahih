package com.sahih.data

class NewsRepository(private val api: NewsApi = NewsApi.create()) {
    suspend fun getScamNews(): List<NewsItem> {
        val feed = api.searchNews(query = "scam OR penipuan OR NSRC OR \"CCID\" Malaysia")
        return feed.channel?.items.orEmpty().map {
            NewsItem(
                title = it.title,
                link = it.link,
                source = it.source?.value ?: "Google News",
                pubDate = it.pubDate
            )
        }.take(5)
    }
}