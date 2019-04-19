/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

// Holds specific information for hashtags, its usefulness is in implementing the PsotInfo interface which allows the
// ListRecycleAdapter and holder to user both HashTags and Users
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