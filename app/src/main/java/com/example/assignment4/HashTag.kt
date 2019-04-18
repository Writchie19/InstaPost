package com.example.assignment4

class HashTag: PostInfo {
    private var hashTag: String
    private var userEmail: String

    constructor(hashTag: String, userEmail: String) {
        this.hashTag = hashTag
        this.userEmail = userEmail
    }

    override fun getUniqueIdentifier() = hashTag
    override fun getItem() = hashTag

    fun getHashTag() = hashTag
    fun setHashTag(newHashTag: String) {hashTag = newHashTag}
}