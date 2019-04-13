package com.example.assignment4

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycle_fragment.*

class RecyclerFragment : Fragment() {

    var layoutManager: LinearLayoutManager? = null
    var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    companion object Factory {
        fun create() : RecyclerFragment {
            val newFragment = RecyclerFragment()
            //newFragment.setView(recyclerAdapter)
//            val args = Bundle()
//            for ((Key,Value) in question) {
//                args.putString(Key, Value)
//            }
//            args.putString("qNumber", qNumber.toString())
//            newFragment.arguments = args
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