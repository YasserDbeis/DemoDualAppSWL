package com.example.demodualappswl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import androidx.window.WindowManager
import com.example.demodualappswl.databinding.ActivitySecondBinding
import java.util.concurrent.Executor

/**
 * This activity is out of commission (it was used to test if instantiating another window manager
 * in a new activity would mess with the SlidingPaneLayout of a different activity. It has since
 * been replaced with TwoPageActivity
 */

class SecondActivity: AppCompatActivity() {

    private lateinit var wm: WindowManager
    private val layoutStateChangeCallback = LayoutStateChangeCallback()
    private lateinit var binding: ActivitySecondBinding

    private fun runOnUiThreadExecutor(): Executor {
        val handler = Handler(Looper.getMainLooper())
        return Executor() {
            handler.post(it)
        }
    }

    override fun onStart() {
        super.onStart()
        wm = WindowManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        wm = WindowManager(this)
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReturnToSLP.setOnClickListener{
            finish()
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

    private fun isDualSeparating(newLayoutInfo: WindowLayoutInfo): Boolean {

        val displayFeatures = newLayoutInfo.displayFeatures

        return if(displayFeatures.isNotEmpty()) {

            val foldingFeature = (displayFeatures.first() as FoldingFeature)
            val isSeparating = foldingFeature.isSeparating

            isSeparating

        } else {
            false
        }
    }

    private fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }
    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun layoutStateChangeHandler(newLayoutInfo: WindowLayoutInfo) {

        binding.tvFoldingFeatureInfo.text = newLayoutInfo.displayFeatures.toString()

    }


    inner class LayoutStateChangeCallback : Consumer<WindowLayoutInfo> {
        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            layoutStateChangeHandler(newLayoutInfo)
        }
    }
}