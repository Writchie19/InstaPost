package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class ListRecycleHolder: RecyclerView.ViewHolder, View.OnClickListener{
    var textView: TextView
    var userPosts: ArrayList<Post>? = null
    private var listRecycleAdapter: ListRecycleAdapter

    constructor(itemView: View, listRecycleAdapter: ListRecycleAdapter) : super(itemView) {
        itemView.setOnClickListener(this)
        textView = itemView.findViewById(R.id.textView)
        this.listRecycleAdapter = listRecycleAdapter
    }

    fun bindInformation(item : PostInfo, userPosts: ArrayList<Post>?) {
        textView.text = item.getItem()
        this.userPosts = userPosts
    }

    override fun onClick(v: View?) {
        Log.i("WCR", "CLICK")
        if (userPosts == null) {
            Log.i("WCR", "NULL")
            val toast = Toast.makeText(listRecycleAdapter.activity, "This User Has No Posts Yet!", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
        }
        else {
            @Suppress("UNCHECKED_CAST")
            listRecycleAdapter.activity.loadFrag(PostRecycleAdapter(userPosts as List<Post>, listRecycleAdapter.activity) as RecyclerView.Adapter<RecyclerView.ViewHolder>)
        }
    }
}