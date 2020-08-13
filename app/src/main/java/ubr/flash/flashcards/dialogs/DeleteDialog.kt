package ubr.flash.flashcards.dialogs

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class DeleteDialog(context: Context){

    val dialog=AlertDialog.Builder(context)
    var listener:((Int)->Unit)?=null

    init {

        dialog.setNegativeButton("Delete forver",DialogInterface.OnClickListener
        { dialog, which ->

            listener?.invoke(0)
            dialog.dismiss()

        })

        dialog.setPositiveButton("PUT BACK",DialogInterface.OnClickListener
        { dialog, which ->

            listener?.invoke(1)
            dialog.dismiss()

        })

        dialog.setMessage("This set in trash, what would you \n like to to?")


        dialog.show()
    }


    fun setDeleteListener(ls:(Int)->Unit){
        listener=ls
    }


}