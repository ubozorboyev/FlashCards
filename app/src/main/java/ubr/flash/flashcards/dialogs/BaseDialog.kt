package ubr.flash.flashcards.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog

abstract class BaseDialog (context: Context,@LayoutRes resId:Int){
private val dialog=AlertDialog.Builder(context).create()
val view:View
    init {
          view=LayoutInflater.from(context).inflate(resId,null,false)
          dialog.setView(view)
    }

    fun show(){
        dialog.show()
    }

    fun dissmis(){
        dialog.dismiss()
    }

}