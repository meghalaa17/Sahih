package com.sahih.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RssFeed(
    @field:Element(name = "channel")
    var channel: RssChannel? = null
)

@Root(name = "channel", strict = false)
data class RssChannel(
    @field:ElementList(inline = true, entry = "item", required = false)
    var items: List<RssItem>? = null
)

@Root(name = "item", strict = false)
data class RssItem(
    @field:Element(name = "title")
    var title: String = "",
    @field:Element(name = "link")
    var link: String = "",
    @field:Element(name = "pubDate", required = false)
    var pubDate: String = "",
    @field:Element(name = "source", required = false)
    var source: SourceTag? = null
)

@Root(name = "source", strict = false)
data class SourceTag(
    @field:org.simpleframework.xml.Text(required = false)
    var value: String = ""
)