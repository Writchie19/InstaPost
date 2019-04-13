package com.example.assignment4

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class ListRecycleHolder : RecyclerView.ViewHolder, View.OnClickListener{
    var textView : TextView? = null

    constructor(itemView: View) : super(itemView) {
        itemView.setOnClickListener(this)
        textView = itemView.findViewById(R.id.textView)
    }

    fun bindText(text : String) {
        textView?.text = text
    }

    override fun onClick(v: View?) {

    }
}