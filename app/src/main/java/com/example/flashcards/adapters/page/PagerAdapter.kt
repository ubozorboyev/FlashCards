package com.example.flashcards.adapters.page

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.flashcards.R

class PagerAdapter :PagerAdapter(){

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return `object`==view
    }

    override fun getCount(): Int {
        return 4
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        Log.d("CCCC","instantiateItem position $position")

        val inflater=LayoutInflater.from(container.context)

        val view=inflater.inflate(R.layout.card_page,container,false)

        container.addView(view)

        return view
//        return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View)
    }

}