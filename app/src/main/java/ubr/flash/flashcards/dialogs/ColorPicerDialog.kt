package ubr.flash.flashcards.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.ColorInt
import ubr.flash.flashcards.R
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener

class ColorPicerDialog(context: Context) :BaseDialog(context, R.layout.color_picer){
    @SuppressLint("SupportAnnotationUsage")
    @ColorInt
    private var listener:((Int)->Unit)?=null
    private val colorPicker:ColorPicker
    private val okButton:Button
    private val cancel:Button
    private val imageView:ImageView
    private var selectColor:Int=0
    init {
        colorPicker=view.findViewById<ColorPicker>(R.id.colorPicker)
        okButton=view.findViewById(R.id.okButtonPicker)
        cancel=view.findViewById(R.id.cancelButtonPicker)
        imageView=view.findViewById(R.id.imageView)
        initslistener()
    }

    fun initslistener(){
        colorPicker.setColorSelectionListener(object :SimpleColorSelectionListener(){

             override fun onColorSelected(color: Int) {
                imageView.background.setTint(color)
                 selectColor=color
            }
        })

        okButton.setOnClickListener {
            if (selectColor!=0){
                listener?.invoke(selectColor)
            }
            dissmis()
        }
        cancel.setOnClickListener { dissmis() }
    }

    fun setColorListener(ls:(Int)->Unit){
        listener=ls
    }

}