package com.example.demodualappswl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.demodualappswl.MainActivity
import com.example.demodualappswl.R

class IssueDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.issue_detail, container, false)
        val close = view.findViewById<Button>(R.id.btnCloseDetail)
        close.setOnClickListener{ (activity as MainActivity).slidingPane.close() }

        return view
    }
}