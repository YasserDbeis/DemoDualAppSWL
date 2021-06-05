/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 *
 */

package com.example.demodualappswl

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.window.DisplayFeature
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import com.example.demodualappswl.adapters.PagerAdapter
import com.example.demodualappswl.fragments.PageFragment
import com.example.demodualappswl.viewModels.PagerViewModel
import java.util.concurrent.Executor


class TwoPageActivity : AppCompatActivity(), OnPageChangeListener {


    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var wm: WindowManager
    private val layoutStateChangeCallback = LayoutStateChangeCallback()

    private lateinit var pagerViewModel: PagerViewModel

    private fun runOnUiThreadExecutor(): Executor {
        val handler = Handler(Looper.getMainLooper())
        return Executor() {
            handler.post(it)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wm = WindowManager(this)

        setupPagerViewModel()

        setupToolbar()

        setupPagerAdapter()

        setContentView(R.layout.activity_two_page)

    }

    private fun setupPagerViewModel() {
        pagerViewModel = ViewModelProviders.of(this)[PagerViewModel::class.java]
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        wm.registerLayoutChangeCallback(
            runOnUiThreadExecutor(),
            layoutStateChangeCallback
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        wm.unregisterLayoutChangeCallback(layoutStateChangeCallback)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    enum class ScreenConfig {
        DualPortrait,
        DualLandscape,
        Single
    }

    inner class LayoutStateChangeCallback : Consumer<WindowLayoutInfo> {

        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            layoutStateChangeHandler(newLayoutInfo)
        }
        private fun layoutStateChangeHandler(newLayoutInfo: WindowLayoutInfo) {

            val isDualScreen = isDualMode(newLayoutInfo)
            val isPortrait = isInPortrait(newLayoutInfo)

            val dualLandscapePager = isDualScreen && !isPortrait
            val screenConfig = if (isDualScreen) {
                if(isPortrait) {
                    ScreenConfig.DualPortrait
                } else {
                    ScreenConfig.DualLandscape
                }
            } else {
                ScreenConfig.Single
            }

            pagerAdapter.showTwoPages = isDualScreen && isPortrait


            if(screenConfig == ScreenConfig.DualPortrait) {

                val foldingFeatureWidth = newLayoutInfo.displayFeatures.first().bounds.width().toFloat()
                val screenWidth = resources.displayMetrics.widthPixels.toFloat()
                val nonHingePercent = ((screenWidth - foldingFeatureWidth).toFloat() / screenWidth.toFloat()).toFloat()

                pagerAdapter.dualPageWidth = (nonHingePercent / 2)
                Log.d("Yasser width %", (nonHingePercent / 2).toString() +
                        " : " + foldingFeatureWidth.toString() +
                        " : " + screenWidth.toString() +
                        " : " + nonHingePercent.toString())

                setupViewPager(false)
                viewPager.pageMargin = newLayoutInfo.displayFeatures.first().bounds.width()

            }
            else {
                setupViewPager(dualLandscapePager)
            }

            // this is customizable to your liking -- in this case I want scroll enabled everywhere
            pagerAdapter.pageContentScrollEnabled = true
        }

        private fun isInPortrait(newLayoutInfo: WindowLayoutInfo) : Boolean {

            val displayFeatures = newLayoutInfo.displayFeatures

            return if(displayFeatures.isNotEmpty()) {
                    val foldingFeature = displayFeatures.first() as FoldingFeature
                    foldingFeature.orientation == FoldingFeature.ORIENTATION_VERTICAL
            } else true
        }

        private fun isDualMode(newLayoutInfo: WindowLayoutInfo) : Boolean {
            for (displayFeature : DisplayFeature in newLayoutInfo.displayFeatures) {
                if (displayFeature is FoldingFeature && displayFeature.occlusionMode == FoldingFeature.OCCLUSION_NONE){
                    return true     // spanned across a fold
                }
                if (displayFeature is FoldingFeature && displayFeature.occlusionMode == FoldingFeature.OCCLUSION_FULL){
                    return true     // spanned across a hinge
                }
            }
            return false
        }

    }

    private fun setupPagerAdapter() {
        val fragments = SparseArray<Fragment>()

        val pageContents = listOf<String>(getString(R.string.two_page_page1_text).repeat(20),
                                        getString(R.string.two_page_page2_text1).repeat(20),
                                        getString(R.string.two_page_page3_text1).repeat(20),
                                        getString(R.string.two_page_page4_text1).repeat(20))

        pageContents.forEachIndexed{index, pageContent ->
            val args = Bundle()
            args.putString("content", pageContent)
            args.putInt("pageNumber", index + 1)

            val pageFragment = PageFragment()
            pageFragment.arguments = args

            fragments.put(index, pageFragment)
        }

        pagerAdapter = PagerAdapter(supportFragmentManager, fragments)
    }

    private fun setupViewPager(dualLandscapePager: Boolean) {
        if (::viewPager.isInitialized) {
            viewPager.adapter = null
        }

//        val pagerID = if(dualLandscapePager) R.id.pager_vert_scroll else R.id.pager_horiz_scroll
        val pagerID = if(false) R.id.pager_vert_scroll else R.id.pager_horiz_scroll

        val currentPage = pagerViewModel.getPageSelectionLiveData().value ?: 0

        viewPager = findViewById<ViewPager>(pagerID).also {
            it.adapter = pagerAdapter
            it.currentItem = currentPage
            it.addOnPageChangeListener(this)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(newPosition: Int) {
        pagerViewModel.setPageSelectionLiveData(newPosition)
    }

    override fun onPageScrollStateChanged(state: Int) {}
}