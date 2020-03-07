package com.example.flashcards.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.badoualy.stepperindicator.StepperIndicator
import com.example.flashcards.R
import com.example.flashcards.adapters.page.PagerAdapter
import com.example.flashcards.util.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.util.prefs.Preferences

class SplashActivity : AppCompatActivity() {

    private var isActive=false
    private var isStart=false
    private val preferences by lazy { applicationContext.getSharedPreferences("PREFERENSE", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isActive=preferences.getBoolean("ISAVIABLE",false)
        isStart=intent.getBooleanExtra("ISAVIABLE",false)

        if (isActive && !isStart){
            startActivity()
        }
        setContentView(R.layout.activity_splash)

        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position==3){
                   Handler().postDelayed(Runnable {
                       startActivity()
                   },1000)
                }
            }

        })

        viewPager.adapter=PagerAdapter()

        indicator.setViewPager(viewPager,viewPager.adapter!!.count)

        viewPager.setPageTransformer(true,ZoomOutPageTransformer())
    }

    fun setPreferense(isAviable:Boolean){
        preferences.edit().putBoolean("ISAVIABLE",isAviable).apply()
    }

    fun startActivity(){
        val intent=Intent(this,MainActivity::class.java)
            setPreferense(true)
            startActivity(intent)
            finish()
    }

}