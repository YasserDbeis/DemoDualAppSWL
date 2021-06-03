/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 *
 */

package com.example.demodualappswl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.example.demodualappswl.R
import kotlinx.android.synthetic.main.fragment_two_page_second_page.*

/**
 * Implementation for the second page
 */
class SecondPageFragment : BasePageFragment() {
    override fun getScrollingContent(): ScrollView = page2_content

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_two_page_second_page, container, false)
    }
}