/*
William Ritchie
CS 646
Assignment 4
4/18/19
 */
package com.example.assignment4

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycle_fragment.*

// The main purpose of this class is to hold and give functionality to update this fragments recycler view
// As well as make navigation in the NavigationActivity easier
class RecyclerFragment : Fragment() {

    var layoutManager: LinearLayoutManager? = null
    var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    // This static constructor is somewhat unnecessary for now, I kept it like this in case for future use i might want it
    companion object Factory {
        fun create() : RecyclerFragment {
            val newFragment = RecyclerFragment()
            return newFragment
        }
    }

    fun setView (recyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, context: Context) {
        layoutManager = LinearLayoutManager(context)
        adapter = recyclerAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycle_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}