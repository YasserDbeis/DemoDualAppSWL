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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.window.DisplayFeature
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager

import com.example.demodualappswl.adapters.PagerAdapter
import com.example.demodualappswl.fragments.FirstPageFragment
import com.example.demodualappswl.fragments.SecondPageFragment
import com.example.demodualappswl.fragments.ThirdPageFragment
import com.example.demodualappswl.fragments.FourthPageFragment
import com.example.demodualappswl.transformers.ZoomOutPageTransformer
import java.util.concurrent.Executor


class TwoPageActivity : AppCompatActivity(), OnPageChangeListener {


    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var wm: WindowManager
    private val layoutStateChangeCallback = LayoutStateChangeCallback()

    private var currPageIndex = 0

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

        setupToolbar()

        setupPagerAdapter()

        setContentView(R.layout.activity_two_page)

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

    inner class LayoutStateChangeCallback : androidx.core.util.Consumer<WindowLayoutInfo> {

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

            pagerAdapter.pageContentScrollEnabled = !isDualScreen || !isPortrait
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

        fragments.put(0, FirstPageFragment())
        fragments.put(1, SecondPageFragment())
        fragments.put(2, ThirdPageFragment())
        fragments.put(3, FourthPageFragment())

        pagerAdapter = PagerAdapter(supportFragmentManager, fragments)
    }

    private fun setupViewPager(dualLandscapePager: Boolean) {
        if (::viewPager.isInitialized) {
            viewPager.adapter = null
        }

        val pagerID = if(dualLandscapePager) R.id.pager_vert_scroll else R.id.pager_horiz_scroll

        viewPager = findViewById<ViewPager>(pagerID).also {
            it.adapter = pagerAdapter
            it.currentItem = currPageIndex
            it.addOnPageChangeListener(this)
        }

        // this is if we want to override the animation (probably dont want to because of the dual screen -- it can get messy)
        // viewPager.setPageTransformer(true, ZoomOutPageTransformer())

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(newPosition: Int) {
        this.currPageIndex = newPosition
    }

    override fun onPageScrollStateChanged(state: Int) {}
}