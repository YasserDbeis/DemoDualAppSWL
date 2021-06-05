/*
 * Copyright (c) Microsoft Corp oration. All rights reserved.
 * Licensed under the MIT License.
 *
 */

package com.example.demodualappswl.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import com.example.demodualappswl.R
import kotlinx.android.synthetic.main.fragment_two_page.*

/**
 * Implementation for the first page
 */
class PageFragment() : BasePageFragment() {
    override fun getScrollingContent(): ScrollView = page_content

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_two_page, container, false)

        view.findViewById<AppCompatTextView>(R.id.page_text).text =
            arguments?.getString("content") ?: "It was null"

        val pageNumber = arguments?.getInt("pageNumber") ?: -1

        if(pageNumber != -1) {
            view.findViewById<AppCompatTextView>(R.id.page_number).text =
                "Page $pageNumber"
        }

//        Log.d("Yasser", arguments?.getString("content") ?: "It was null")

        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val test = getScrollingContent()

        Log.d("Yasser", (test == null).toString())
    }
}