package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListRecycleAdapter : RecyclerView.Adapter<ListRecycleHolder> {
    var itemList : List<String>? = null
    var activity : NavigationActivity? = null

    constructor(itemList: List<String>, activity : NavigationActivity) {
        this.itemList = itemList
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecycleHolder {
        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ListRecycleHolder(view)
    }

    override fun onBindViewHolder(holder : ListRecycleHolder, position : Int) {
        val item = itemList?.get(position) as String
        holder.bindText(item)
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}