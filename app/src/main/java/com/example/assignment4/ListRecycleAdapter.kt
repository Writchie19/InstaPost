package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListRecycleAdapter : RecyclerView.Adapter<ListRecycleHolder> {
    var itemList : List<PostInfo>
    var activity : NavigationActivity
    var userPosts : HashMap<String, ArrayList<Post>>

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
        holder.bindInformation(item, userPosts[item.getUniqueIdentifier()])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}