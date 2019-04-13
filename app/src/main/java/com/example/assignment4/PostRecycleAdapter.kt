package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class PostRecycleAdapter : RecyclerView.Adapter<PostRecycleHolder> {
    var postList : List<Post>? = null
    var activity : MainActivity? = null

    constructor(posts: List<Post>, activity : MainActivity) {
        this.postList = posts
        this.activity = activity
    }

    override fun toString(): String {
        return postList.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRecycleHolder {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.posts, parent, false)
        return PostRecycleHolder(view)
    }

    override fun onBindViewHolder(holder : PostRecycleHolder, position : Int) {
        val post = postList?.get(position) as Post
        holder.bindPost(post)
    }

    override fun getItemCount(): Int {
        return postList?.size ?: 0
    }
}