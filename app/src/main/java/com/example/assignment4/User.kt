/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

// This is the interface implemented by User and Hashtag classes to allow both objects to be used by ListRecycler Adapter and Holder
interface PostInfo {
    fun getItem():String // Should be used to update some sort of display information
    fun getUniqueIdentifier():String // Should be used to access a specific subset of posts
}

class User: PostInfo{
    private var userName: String
    private var userNickName: String
    private var userEmail: String

    constructor(userName: String, userNickName: String, userEmail: String) {
        this.userName = userName
        this.userNickName = userNickName
        this.userEmail = userEmail
    }

    fun getUserName() = userName
    fun getUserNickName() = userNickName
    fun getUserEmail() = userEmail
    override fun getItem() = userNickName
    override fun getUniqueIdentifier() = userEmail

    fun setUserName(newName: String) {userName = newName}
    fun setUserNickName(newNickName: String) {userNickName = newNickName}
    fun setUserEmail(newEmail: String) {userEmail = newEmail}
}