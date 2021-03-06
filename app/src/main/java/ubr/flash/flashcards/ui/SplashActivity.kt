package ubr.flash.flashcards.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.viewpager.widget.ViewPager
import ubr.flash.flashcards.R
import ubr.flash.flashcards.adapters.page.PagerAdapter
import ubr.flash.flashcards.util.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.activity_splash.*

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

        viewPager.adapter=PagerAdapter(applicationContext)


        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                viewPager.setBackgroundColor(when(position){
                    0->Color.parseColor("#F12626")
                    1->Color.parseColor("#E1A44B")
                    2->Color.parseColor("#3D713F")
                    3->Color.parseColor("#E1A44B")
                    else ->Color.LTGRAY
                })

            }

            override fun onPageSelected(position: Int) {
                if (position==3){
                   Handler().postDelayed(Runnable {
                       startActivity()
                   },1000)
                }
            }

        })


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