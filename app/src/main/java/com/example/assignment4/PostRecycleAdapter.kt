/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class PostRecycleAdapter : RecyclerView.Adapter<PostRecycleHolder> {
    var postList : List<Post>? = null // The posts that are to be displayed
    var activity : NavigationActivity? = null

    constructor(posts: List<Post>?, activity : NavigationActivity) {
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

    // Bind the holder with the specific post it is charged with
    override fun onBindViewHolder(holder : PostRecycleHolder, position : Int) {
        val post = postList?.get(position) as Post
        holder.bindPost(post)
    }

    override fun getItemCount(): Int {
        return postList?.size ?: 0
    }
}