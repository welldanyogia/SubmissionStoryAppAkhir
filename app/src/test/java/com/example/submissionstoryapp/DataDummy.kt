package com.example.submissionstoryapp

import com.example.submissionstoryapp.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "createdAt $i",
                "name $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(storyItem)
        }
        return items
    }

}