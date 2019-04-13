package com.example.assignment4

import android.net.Uri

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

    fun setImage(image: Uri) {
        this.image = image
    }

    fun getImage(): Uri? {
        return image
    }

    override fun toString(): String {
        return imagePath as String
    }
}