package com.dicoding.storyapp

import com.dicoding.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt $i",
                "name $i",
                "description $i",
                lon = i.toDouble(),
                "id + $i",
                lat = i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}