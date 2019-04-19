/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class PostRecycleHolder : RecyclerView.ViewHolder{
    var nickNameView : TextView? = null
    var descriptionView : TextView? = null
    var image : ImageView? = null

    constructor(itemView: View) : super(itemView) {
        nickNameView = itemView.findViewById(R.id.nicknameView)
        descriptionView = itemView.findViewById(R.id.descriptionView)
        image = itemView.findViewById(R.id.imageView)
    }

    // Updates the UI information with the post specific information
    fun bindPost(post : Post) {
        nickNameView?.text = post.nickName
        descriptionView?.text = post.description
        image?.setImageURI(post.getImage())
    }
}