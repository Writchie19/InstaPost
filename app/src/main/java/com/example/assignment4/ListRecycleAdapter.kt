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

// Used for listing the list of users and list of hashtags
class ListRecycleAdapter : RecyclerView.Adapter<ListRecycleHolder> {
    var itemList : List<PostInfo> // This is the list of HashTag or User objects
    var activity : NavigationActivity
    var userPosts : HashMap<String, ArrayList<Post>> // The Key for the hashmap is dependent on whether the PostInfo objects
    // are hashtags or users

    constructor(itemList: List<PostInfo>,userPosts: HashMap<String, ArrayList<Post>>, activity : NavigationActivity) {
        this.itemList = itemList
        this.activity = activity
        this.userPosts = userPosts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecycleHolder {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ListRecycleHolder(view, this)
    }

    override fun onBindViewHolder(holder : ListRecycleHolder, position : Int) {
        val item = itemList[position]
        holder.bindInformation(item, userPosts[item.getUniqueIdentifier()]) // This gives the holder the specific list of posts that it needs
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}