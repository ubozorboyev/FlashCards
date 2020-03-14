package com.example.flashcards.adapters.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.flashcards.R

class PagerAdapter(val appContext:Context) :PagerAdapter(){

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return `object`==view
    }

    override fun getCount(): Int {
        return 4
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater=LayoutInflater.from(container.context)

        val view=inflater.inflate(R.layout.card_page,container,false)

        val textOne=view.findViewById<TextView>(R.id.textOne)
        val textTwo=view.findViewById<TextView>(R.id.textTwo)
        val imageView=view.findViewById<ImageView>(R.id.imageView)

        textOne.text=when(position){
            0->appContext.getString(R.string.page1)
            1->appContext.getString(R.string.page2)
            2->appContext.getString(R.string.page3)
            3->appContext.getString(R.string.page4)
            else -> ""
        }
        textTwo.text=when(position){
            0->appContext.getString(R.string.pageOne)
            1->appContext.getString(R.string.pageTwo)
            2->appContext.getString(R.string.pageTree)
            3->appContext.getString(R.string.pageFo)
            else->""
        }

        imageView.setImageResource(
            when(position){
                0->R.drawable.page1
                1->R.drawable.page2
                2->R.drawable.page3
                3->R.drawable.page4
                else->R.drawable.page2
            }
        )
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View)
    }

}