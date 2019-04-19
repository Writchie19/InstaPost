/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.net.Uri

// The most important thing this class does is hold on to the image URI information for a specific post
class Post {
    var nickName : String? = null
    var imagePath : String? = null
    var description : String? = null
    private var image : Uri? = null

    constructor(nickName: String, imagePath : String, description : String) {
        this.nickName = nickName
        this.imagePath = imagePath
        this.description = description
    }

    fun setImageUri(image: Uri) {
        this.image = image
    }

    fun getImage(): Uri? {
        return image
    }

    override fun toString(): String {
        return imagePath as String
    }
}