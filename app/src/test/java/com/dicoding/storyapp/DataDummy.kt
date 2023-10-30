package com.dicoding.storyapp

import com.dicoding.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items = ArrayList<ListStoryItem>()

        for (i in 0..100) {
            val story = ListStoryItem(
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.dicoding.com%2Fblog%2Fauthor%2Fdicoding-intern%2F&psig=AOvVaw3nWVtfPj8RGUDT8Qa43j03&ust=1698751106528000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCNC_g5TTnYIDFQAAAAAdAAAAABAE",
                "2023-10-30T21:33:02.711Z",
                "Putri",
                "Deskripsi",
                -6.2991235,
                "story $i",
                106.9039209
            )
            items.add(story)
        }
        return items
    }
}