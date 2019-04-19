/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast

// Used by the ListRecycleAdapter to hold each individual item on the desired list
// Also, has the functionality to alter the current fragment with specific posts if the user clicks on this holder
class ListRecycleHolder: RecyclerView.ViewHolder, View.OnClickListener{
    var textView: TextView // The text that is displayed each holder
    var userPosts: ArrayList<Post>? = null // This will be set to the posts associated with this holder, might be null if there a no posts
    private var listRecycleAdapter: ListRecycleAdapter // Need reference to this inorder to access the activity

    constructor(itemView: View, listRecycleAdapter: ListRecycleAdapter) : super(itemView) {
        itemView.setOnClickListener(this)
        textView = itemView.findViewById(R.id.textView)
        this.listRecycleAdapter = listRecycleAdapter
    }

    // Called by the ListRecycleAdapter to give this holder the information it needs to function, (i.e its text and the associated posts)
    fun bindInformation(item : PostInfo, userPosts: ArrayList<Post>?) {
        textView.text = item.getItem()
        this.userPosts = userPosts
    }

    override fun onClick(v: View?) {
        // Need the null check in case a user has not posted anything
        if (userPosts == null) {
            val toast = Toast.makeText(listRecycleAdapter.activity, "This User Has No Posts Yet!", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
        }
        else {
            // This loads a new fragment to replace the current one, the input to the fragment is a recycle adapter which in this case
            // has an input of the specific posts taht are to be viewed
            @Suppress("UNCHECKED_CAST")
            listRecycleAdapter.activity.loadFrag(PostRecycleAdapter(userPosts as List<Post>, listRecycleAdapter.activity) as RecyclerView.Adapter<RecyclerView.ViewHolder>)
        }
    }
}