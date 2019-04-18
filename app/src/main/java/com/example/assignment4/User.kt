package com.example.assignment4

interface PostInfo {
    fun getItem():String
    fun getUniqueIdentifier():String
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