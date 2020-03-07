package com.example.flashcards.util

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class CustomLayoutManager(private val context: Context, layoutDirection: Int):
    LinearLayoutManager(context, layoutDirection, false) {

    companion object {
        // This determines how smooth the scrolling will be
        private
        const val MILLISECONDS_PER_INCH = 2f
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {

        val smoothScroller: LinearSmoothScroller = object: LinearSmoothScroller(context) {

            fun dp2px(dpValue: Float): Int {
                val scale = context.resources.displayMetrics.density
                return (dpValue * scale + 0.5f).toInt()
            }

            // change this and the return super type to "calculateDyToMakeVisible" if the layout direction is set to VERTICAL
            override fun calculateDxToMakeVisible(view: View?, snapPreference : Int): Int {
                return super.calculateDxToMakeVisible(view, SNAP_TO_END) - dp2px(50f)
            }

            //This controls the direction in which smoothScroll looks for your view
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@CustomLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        Log.d("TTTT","transformPage position $position")

        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    Log.d("TTTY","// This page is way off-screen to the right.")
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}